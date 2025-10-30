package controller;

import constant.Rank;
import constant.Suit;
import dao.HistoryDAO;
import dao.UserDAO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import model.*;
import model.Message.MessageType;
import model.User.PlayerStatus;

public class GameRoomHandler extends Thread {
  private final ServerMain server;
  private final ClientHandler player1;
  private final ClientHandler player2;
  private volatile boolean isGameRunning = true; // Cờ để kiểm soát vòng lặp game an toàn
  private final UserDAO userDAO = new UserDAO();
  private final HistoryDAO historyDAO = new HistoryDAO();

  private final BlockingQueue<Message> actionQueue;
  private List<Card> deck;
  private final List<Card> player1Hand = new ArrayList<>();
  private final List<Card> player2Hand = new ArrayList<>();

  public GameRoomHandler(ClientHandler player1, ClientHandler player2, ServerMain server) {
    this.player1 = player1;
    this.player2 = player2;
    this.server = server;
    this.actionQueue = new LinkedBlockingQueue<>();
  }

  @Override
  public void run() {
    try {
      System.out.println("GameRoom: Cả hai người chơi đã vào phòng. Bắt đầu sau 2 giây...");
      Thread.sleep(2000);

      startRound();

      for (int turn = 0; isGameRunning && turn < 6; turn++) {
        ClientHandler playerInTurn = (turn % 2 == 0) ? player1 : player2;

        broadcast(new Message(MessageType.TURN_UPDATE, playerInTurn.getUser().getUsername()));
        Message action = actionQueue.poll(15, TimeUnit.SECONDS);

        if (action == null) {
          handleForfeit(playerInTurn, "Hết giờ.");
          return;
        }

        if (action.getType() == MessageType.EXIT_GAME) {
          String exitingUsername = (String) action.getPayload();
          ClientHandler exitingPlayer = server.getClient(exitingUsername);
          if (exitingPlayer != null) {
            handleForfeit(exitingPlayer, "đã chủ động thoát.");
          }
          return;
        }

        if (action.getType() != MessageType.PICK_CARD) {
          handleForfeit(playerInTurn, "Hành động không hợp lệ.");
          return;
        }

        int cardIndex = (int) action.getPayload();
        if (cardIndex < 0 || cardIndex >= 10) {
          handleForfeit(playerInTurn, "Gửi index của lá bài không hợp lệ.");
          return;
        }

        if (deck.isEmpty()) break;

        Card pickedCard = deck.removeFirst();
        if (playerInTurn == player1) {
          player1Hand.add(pickedCard);
        } else {
          player2Hand.add(pickedCard);
        }

        broadcast(new Message(MessageType.CARD_PICKED_UPDATE, new CardUpdateInfo(
            playerInTurn.getUser().getUsername(),
            pickedCard,
            cardIndex
        )));
      }

      if (isGameRunning) {
        calculateAndSendResult();
      }

    } catch (InterruptedException e) {
      System.out.println("GameRoom: Luồng game bị ngắt.");
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      endTheGame();
    }
  }


  public synchronized void handlePlayerDisconnect(ClientHandler disconnectedPlayer) {
    if (!isGameRunning) return;
    ClientHandler winner = (disconnectedPlayer == player1) ? player2 : player1;
    endGameByDefault(winner, disconnectedPlayer, "đã thoát khỏi trận đấu.");
  }

  private void handleForfeit(ClientHandler forfeitingPlayer, String reason) {
    if (!isGameRunning) return;
    ClientHandler winner = (forfeitingPlayer == player1) ? player2 : player1;
    endGameByDefault(winner, forfeitingPlayer, "đã bỏ cuộc (" + reason + ").");
  }


  private void endGameByDefault(ClientHandler winner, ClientHandler loser, String reason) {
    isGameRunning = false;
    User winnerUser = winner.getUser();
    User loserUser = loser.getUser();

    System.out.println("GameRoom: " + winnerUser.getUsername() + " thắng do " + loserUser.getUsername() + " " + reason);

    History h = new History();
    h.setPlayer1(winnerUser);
    h.setPlayer2(loserUser);
    h.setScore1(1);
    h.setScore2(0);

    if(historyDAO.addHistory(h)){
        System.out.println("Lưu lịch sử đấu thành công!");
    } else {
        System.out.println("Lưu lịch sử thất bại!");
    }


    userDAO.addScore(winnerUser.getId(), 1);
    winnerUser.setScore(winnerUser.getScore() + 1);

    String victoryReason = "Bạn đã thắng do đối thủ '" + loserUser.getUsername() + "' " + reason;
    try {
      winner.sendMessage(new Message(MessageType.GAME_FORFEIT, victoryReason));
    } catch (IOException e) {  }

    server.sendPlayerList();

    this.interrupt();
  }

  private void startRound() {
    player1Hand.clear();
    player2Hand.clear();
    deck = new ArrayList<>();
    for (Rank rank : Rank.values()) {
      for (Suit suit : Suit.values()) {
        deck.add(new Card(rank, suit));
      }
    }
    Collections.shuffle(this.deck);
  }

  private void calculateAndSendResult() {
    int player1Score = player1Hand.stream().mapToInt(card -> card.getRank().getSumValue()).sum() % 10;
    int player2Score = player2Hand.stream().mapToInt(card -> card.getRank().getSumValue()).sum() % 10;

    User winner = null;
    if (player1Score > player2Score) {
      winner = player1.getUser();
    } else if (player2Score > player1Score) {
      winner = player2.getUser();
    } else {
      Collections.sort(player1Hand);
      Collections.sort(player2Hand);
      Card p1HighestCard = player1Hand.get(player1Hand.size() - 1);
      Card p2HighestCard = player2Hand.get(player2Hand.size() - 1);
      if (p1HighestCard.compareTo(p2HighestCard) > 0) {
        winner = player1.getUser();
      } else {
        winner = player2.getUser();
      }
    }

    if (winner != null) {
      System.out.println("Winner is " + winner.getUsername());
      userDAO.addScore(winner.getId(), 1);
      winner.setScore(winner.getScore() + 1);
    }

    GameResult gameResult = new GameResult(
        winner != null ? winner.getUsername() : "Hòa",
        player1.getUser().getUsername(),
        player2.getUser().getUsername(),
        player1Hand,
        player2Hand,
        player1Score,
        player2Score
    );
    broadcast(new Message(MessageType.GAME_RESULT, gameResult));
    server.sendPlayerList();
  }

  private void endTheGame() {
    System.out.println("GameRoom: Game ended. Cleaning up.");
    player1.leaveRoom();
    player2.leaveRoom();
    player1.getUser().setStatus(PlayerStatus.AVAILABLE);
    player2.getUser().setStatus(PlayerStatus.AVAILABLE);
    server.sendPlayerList();
  }

  public void postPlayerAction(Message receivedMessage) {
    actionQueue.offer(receivedMessage);
  }

  private void broadcast(Message message) {
    try {
      player1.sendMessage(message);
      player2.sendMessage(message);
    } catch (IOException e) {
      System.err.println("GameRoom: Lỗi khi gửi broadcast, một người chơi có thể đã ngắt kết nối.");
    }
  }
}