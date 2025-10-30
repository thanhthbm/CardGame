package com.thanhthbm.cardgame.controller;

import com.thanhthbm.cardgame.context.AppContext;
import com.thanhthbm.cardgame.constants.Screen;
import com.thanhthbm.cardgame.net.ClientListener;

import com.thanhthbm.cardgame.net.GameClient;
import constant.Rank;
import constant.Suit;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import model.Card;
import model.DTO.CardUpdateInfo;
import model.DTO.GameResult;
import model.DTO.Message;
import model.DTO.Message.MessageType;

public class GameController implements ClientListener {
  @FXML private AnchorPane gamePane;
  @FXML private HBox cardsContainer;
  @FXML private Label player1NameLabel;
  @FXML private Label player2NameLabel;
  @FXML private Label statusLabel;
  @FXML private ProgressBar timerBar;
  @FXML private Label timerLabel;
  @FXML private VBox timerBox;
  @FXML private ImageView card0, card1, card2, card3, card4, card5, card6, card7, card8, card9;

  private List<ImageView> cardImageViews;
  private GameClient client;
  private boolean isMyTurn = false;
  private Timeline countdownTimeline;
  private final IntegerProperty timeLeft = new SimpleIntegerProperty();



  @FXML
  private void initialize() {
    cardImageViews = Arrays.asList(card0, card1, card2, card3, card4, card5, card6, card7, card8, card9);
    this.client = AppContext.getInstance().getClient();
    client.setListener(this);
    player1NameLabel.setText(AppContext.getInstance().getStartInfo().getPlayer1Name());
    player2NameLabel.setText(AppContext.getInstance().getStartInfo().getPlayer2Name());
    setupTimer();
  }

  private void setupTimer() {
    timerLabel.textProperty().bind(timeLeft.asString());
    timerBar.progressProperty().bind(timeLeft.divide(10.0));

    countdownTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
      timeLeft.set(timeLeft.get() - 1);
    }));
    countdownTimeline.setCycleCount(10);
    countdownTimeline.setOnFinished(event -> {
      isMyTurn = false;
      statusLabel.setText("Hết giờ!");
      timerBox.setVisible(false);
    });
  }

  @FXML
  private void handleCardClick(MouseEvent event) {
    if (!isMyTurn) return;

    ImageView clickedCard = (ImageView) event.getSource();
    if (clickedCard.getUserData() != null) return;

    countdownTimeline.stop();
    timerBox.setVisible(false);

    int cardIndex = Integer.parseInt(clickedCard.getId().replace("card", ""));

    client.sendMessage(new Message(MessageType.PICK_CARD, cardIndex));

    isMyTurn = false;
    statusLabel.setText("Đang chờ đối thủ...");
  }

  @Override
  public void onConnected() {
    System.out.println("Join room");
  }

  @Override
  public void onDisconnected(String e) {
    System.out.println("Exit room");
  }

  @Override
  public void onMessageReceived(Message message) {
    Platform.runLater(() -> {
      switch (message.getType()) {
        case TURN_UPDATE:
          String userTurn = (String) message.getPayload();
          statusLabel.setText("Tới lượt của " + userTurn);
          isMyTurn = userTurn.equalsIgnoreCase(AppContext.getInstance().getCurrentUser().getUsername());

          if (isMyTurn) {
            timerBox.setVisible(true);
            timeLeft.set(10);
            countdownTimeline.playFromStart();
          } else {
            timerBox.setVisible(false);
            countdownTimeline.stop();
          }
          break;

        case CARD_PICKED_UPDATE:
          if (message.getPayload() instanceof CardUpdateInfo) {
            CardUpdateInfo info = (CardUpdateInfo) message.getPayload();
            flipCard(info.getCardIndex(), info.getPickedCard());
          }
          break;

        case GAME_RESULT:
          if (message.getPayload() instanceof GameResult) {
            showResultDialog((GameResult) message.getPayload());
          }
          break;
        case GAME_FORFEIT:
          countdownTimeline.stop();
          timerBox.setVisible(false);
          isMyTurn = false;

          if (message.getPayload() instanceof String) {
            String reason = (String) message.getPayload();
            Alert forfeitAlert = new Alert(Alert.AlertType.INFORMATION);
            forfeitAlert.setTitle("Bạn đã thắng");
            forfeitAlert.setHeaderText(null);
            forfeitAlert.setContentText(reason);
            forfeitAlert.initOwner(gamePane.getScene().getWindow());
            forfeitAlert.getDialogPane().lookup(".content.label").setStyle("-fx-text-fill: black;");

            forfeitAlert.showAndWait().ifPresent(response -> {
              LobbyController lobbyController = SceneManager.switchScene(Screen.LOBBY);
              if (lobbyController != null) {
                lobbyController.refresh();
              }
            });
          }
          break;
      }
    });
  }

  private void flipCard(int index, Card card) {
    if (index >= 0 && index < cardImageViews.size()) {
      ImageView imageView = cardImageViews.get(index);
      Image faceImage = getImageForCard(card);
      if (faceImage != null) {
        imageView.setImage(faceImage);
        imageView.setUserData("flipped");
      }
    }
  }

  private Image getImageForCard(Card card) {
    Rank rank = card.getRank();
    Suit suit = card.getSuit();

    String suitString = suit.name().substring(0, 1) + suit.name().substring(1).toLowerCase();

    String rankString;
    switch (rank) {
      case ACE:   rankString = "Ace";   break;
      case KING:  rankString = "King";  break;
      case QUEEN: rankString = "Queen"; break;
      case JACK:  rankString = "Jack";  break;
      default:
        rankString = String.valueOf(rank.getCompareValue());
        break;
    }

    String fileName = "Suit=" + suitString + ", Number=" + rankString + ".png";

    String path = "../images/card/" + fileName;

    try {
      InputStream stream = getClass().getResourceAsStream(path);
      if (stream == null) {
        System.err.println("Không tìm thấy file ảnh: " + path);
        return null;
      }
      return new Image(stream);
    } catch (Exception e) {
      System.err.println("Lỗi khi tải ảnh: " + path);
      e.printStackTrace();
      return null;
    }
  }

  private void showResultDialog(GameResult result) {
    String myUsername = AppContext.getInstance().getCurrentUser().getUsername();
    boolean iAmWinner = myUsername.equals(result.getWinnerUsername());



    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Kết quả ván đấu");
    alert.setHeaderText(iAmWinner ? "Chúc mừng, bạn đã thắng!" : "Bạn đã thua!");

    String content = String.format(
        "Người thắng: %s\n\n" +
            "Bài của %s (Điểm: %d): %s\n" +
            "Bài của %s (Điểm: %d): %s",
        result.getWinnerUsername(),
        result.getPlayer1Username(), result.getPlayer1Score(), result.getPlayer1Hand(),
        result.getPlayer2Username(), result.getPlayer2Score(), result.getPlayer2Hand()
    );
    alert.setContentText(content);

    alert.initOwner(gamePane.getScene().getWindow());

    alert.showAndWait().ifPresent(response -> {
      LobbyController lobbyController = SceneManager.switchScene(Screen.LOBBY);

      if (lobbyController != null) {
        lobbyController.refresh();
      }
    });
  }

  @FXML
  private void onExitGame(ActionEvent event) {
    System.out.println("Exit game requested.");
    Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
    confirmAlert.setTitle("Xác nhận Thoát");
    confirmAlert.setHeaderText("Bạn có chắc chắn muốn thoát khỏi trận đấu?");
    confirmAlert.setContentText("Bạn sẽ bị xử thua nếu thoát ngay bây giờ.");
    confirmAlert.initOwner(gamePane.getScene().getWindow());

    confirmAlert.showAndWait().ifPresent(response -> {
      if (response == ButtonType.OK) {
        client.sendMessage(new Message(MessageType.EXIT_GAME, AppContext.getInstance().getCurrentUser().getUsername()));

        LobbyController lobbyController = SceneManager.switchScene(Screen.LOBBY);
        if (lobbyController != null) {
          lobbyController.refresh();
        }
      }
    });
  }
}
