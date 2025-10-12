import constant.Rank;
import constant.Suit;
import dao.UserDAO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import model.Card;
import model.CardUpdateInfo;
import model.GameResult;
import model.Message;
import model.Message.MessageType;
import model.User;
import model.User.PlayerStatus;

public class GameRoomHandler extends Thread {
  private final ServerMain server;
  private final ClientHandler player1;
  private final ClientHandler player2;
  private volatile int currentTurn = 0;
  private final UserDAO userDAO = new UserDAO();

  private final BlockingQueue<Message> actionQueue;

  private List<Card> deck;

  private final List<Card> player1Hand = new ArrayList<Card>();
  private final List<Card> player2Hand = new ArrayList<Card>();

  public GameRoomHandler(ClientHandler player1, ClientHandler player2, ServerMain server){
    this.player1 = player1;
    this.player2 = player2;
    this.server = server;
    this.actionQueue = new LinkedBlockingQueue<>();
  }

  private void startRound(){
    //xóa bài cũ của 2 người chơi, vì họ có thể đã chơi trước 1 round rồi?
    player1Hand.clear();
    player2Hand.clear();

    //Tạo bộ bài
    for (Rank rank : Rank.values()){
      for (Suit suit : Suit.values()){
        Card card = new Card(rank, suit);
        this.deck.add(card);
      }
    }

    //xáo bài
    Collections.shuffle(this.deck);
    System.out.println("Game start!");
  }

  @Override
  public void run() {
    try{
      deck = new ArrayList<>();
      startRound();
      //6 luợt bốc bài
      int cnt = 0;
      for (int turn = 0; turn < 6; turn++){
        ClientHandler playerInTurn;
        if (turn % 2 == 0){
          //Người chơi 1 sẽ được đánh trước =))
          playerInTurn = player1;
        } else {
          playerInTurn = player2;
        }

        if (cnt == 0){
          Thread.sleep(2000);
          cnt++;
        }

        broadcast(new Message(MessageType.TURN_UPDATE, playerInTurn.getUser().getUsername()));

        Message action = actionQueue.poll(10, TimeUnit.SECONDS);

        if (action == null || action.getType() != MessageType.PICK_CARD) {
          handleForfeit(playerInTurn, "Hết giờ hoặc hành động không hợp lệ.");
          return;
        }

        int cardIndex = (int) action.getPayload();

        if (cardIndex < 0 || cardIndex >= 10) {
          handleForfeit(playerInTurn, "Gửi index của lá bài không hợp lệ.");
          return;
        }

        if (deck.isEmpty()){
          break;
        }

        Card pickedCard = deck.removeFirst();
        if (playerInTurn == player1){
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

      //sau 6 lần bốc bài, tính toán kết quả và thắng thua
      calculateAndSendResult();


    } catch (Exception e){
      e.printStackTrace();

    }
  }

  public synchronized ClientHandler getCurrentPlayerInTurn(){
    if (currentTurn % 2 == 0){
      return player1;
    }
    return player2;
  }

  private void broadcast(Message message) {
    try {
      player1.sendMessage(message);
      player2.sendMessage(message);
    } catch (IOException e) {
      System.err.println("GameRoom: Lỗi khi gửi broadcast, một người chơi có thể đã ngắt kết nối.");
    }
  }

  public void postPlayerAction(Message receivedMessage) {
    actionQueue.offer(receivedMessage);
  }

  private void handleForfeit(ClientHandler forfeitingPlayer, String reason) {
    System.out.println(forfeitingPlayer.getUser().getUsername() + " has forfeited: " + reason);
    ClientHandler winner = (forfeitingPlayer == player1) ? player2 : player1;

    // TODO: Xử lý cộng điểm cho người thắng và gửi message GAME_FORFEIT
  }

  private void endTheGame() {
    System.out.println("GameRoom: Game ended. Cleaning up.");
    player1.leaveRoom();
    player2.leaveRoom();
    player1.getUser().setStatus(PlayerStatus.AVAILABLE);
    player2.getUser().setStatus(PlayerStatus.AVAILABLE);
    server.sendPlayerList();
  }

  private void calculateAndSendResult(){
    int player1Score = player1Hand.stream().mapToInt(card -> card.getRank().getSumValue()).sum() % 10;
    int player2Score = player2Hand.stream().mapToInt(card -> card.getRank().getSumValue()).sum() % 10;

    User winner = null;
    User loser = null;

    if (player1Score > player2Score){
      winner = player1.getUser();
      loser = player2.getUser();
    } else if (player2Score > player1Score){
      winner = player2.getUser();
      loser = player1.getUser();
    } else {
      // trường hợp tổng điểm bằng nhau
      // sắp xếp bộ bài lấy ra lá lớn nhất
      Collections.sort(player1Hand);
      Collections.sort(player2Hand);

      Card p1HighestCard = player1Hand.get(player1Hand.size() - 1);
      Card p2HighestCard = player2Hand.get(player2Hand.size() - 1);

      if (p1HighestCard.compareTo(p2HighestCard) > 0){
        winner = player1.getUser();
        loser = player2.getUser();
      } else {
        winner = player2.getUser();
        loser = player1.getUser();

      }
    }

    if (winner != null){
      System.out.println("Winner is " + winner.getUsername());
      //thắng cộng thêm 1 điểm
      userDAO.addScore(winner.getId(), 1);
      winner.setScore(winner.getScore() + 1);
    }

    GameResult gameResult = new GameResult(
        winner != null ? winner.getUsername() : "Hòa",
        player1.getUser().getUsername(), // Gửi tên player1
        player2.getUser().getUsername(), // Gửi tên player2
        player1Hand,
        player2Hand,
        player1Score,
        player2Score
    );

    broadcast(new Message(MessageType.GAME_RESULT, gameResult));

    server.sendPlayerList();
  }
}
