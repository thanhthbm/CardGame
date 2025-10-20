package com.thanhthbm.cardgame.controller;

import com.thanhthbm.cardgame.context.AppContext;
import com.thanhthbm.cardgame.constants.Screen;
import com.thanhthbm.cardgame.net.ClientListener;
import com.thanhthbm.cardgame.net.GameClient;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import model.ChallengeResponse;
import model.GameStartInfo;
import model.Message;
import model.Message.MessageType;
import model.User;
import model.User.PlayerStatus;

public class LobbyController implements ClientListener {
  @FXML
  public AnchorPane lobbyPane;
  @FXML
  private ListView<User> playersListView;
  private GameClient client;

  private ObservableList<User> playersList = FXCollections.observableArrayList();

  @FXML
  public void initialize() {
    this.client = AppContext.getInstance().getClient();
    client.setListener(this);

    playersListView.setItems(playersList);
    playersListView.setCellFactory(listView ->  new PlayerListCell());

    Message m = new Message(MessageType.GET_ONLINE_LIST, null);
    client.sendMessage(m);

  }

  @Override
  public void onConnected() {
    System.out.println("Lobby connected");
  }

  @Override
  public void onDisconnected(String e) {
    System.out.println("Lobby disconnected");
  }

  @Override
  public void onMessageReceived(Message message) {
    Platform.runLater(() -> {
      switch (message.getType()) {
        case ONLINE_LIST:
          if (message.getPayload() instanceof List) {
            List<User> updatedPlayers = (List<User>) message.getPayload();

            List<User> renderList = new ArrayList<>();
            for (User u : updatedPlayers) {
              if (!u.getUsername().equals(AppContext.getInstance().getCurrentUser().getUsername())) {
                renderList.add(u);
              }
            }

            playersList.setAll(renderList);
          }
          break;

        case CHALLENGE_REQUEST:
          if (message.getPayload() instanceof String) {
            String sender = (String) message.getPayload();
            boolean accepted = getChallengeResponse(sender);

            ChallengeResponse response = new ChallengeResponse(sender, accepted);
            Message m = new Message(MessageType.CHALLENGE_RESPONSE, response);
            client.sendMessage(m);
          }
          break;
        case CHALLENGE_FAILED:
          if (message.getPayload() instanceof String) {
            String reason = (String) message.getPayload();
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Người chơi từ chối");
            alert.setHeaderText(null);
            alert.setContentText(reason);
            alert.showAndWait();
          }
          break;
        case CHALLENGE_SUCCESS:
          if (message.getPayload() instanceof String) {
            String reason = (String) message.getPayload();
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Người chơi chấp nhận");
            alert.setHeaderText(null);
            alert.setContentText(reason);
            alert.showAndWait();
          }
          break;

        case GAME_START:
          GameStartInfo startInfo = (GameStartInfo) message.getPayload();
          AppContext.getInstance().setStartInfo(startInfo);
          SceneManager.switchScene(Screen.GAME);
          break;
      }
    });
  }

  public void onBack(ActionEvent actionEvent) {
    SceneManager.switchScene(Screen.HOME);
  }

  private class PlayerListCell extends ListCell<User> {
    private HBox hBox = new HBox(10);
    private ImageView avatar = new ImageView();
    private Label nameLabel = new Label();
    private Circle statusIndicator = new Circle(8);
    private Label statusLabel = new Label();
    private Button challengeButton = new Button("Thách đấu");
    private Region spacer =  new Region();

    public PlayerListCell() {
      super();
      avatar.setFitWidth(40);
      avatar.setFitHeight(40);
      avatar.setPreserveRatio(true);
      avatar.setClip(new Circle(20, 20, 20));
      hBox.setAlignment(Pos.CENTER_LEFT);
      HBox.setHgrow(spacer, Priority.ALWAYS);

      // Thêm các thành phần vào HBox
      hBox.getChildren().addAll(avatar, nameLabel, spacer, statusIndicator, statusLabel, challengeButton);

      // Xử lý sự kiện khi nút "Thách đấu" được nhấn
      challengeButton.setOnAction(event -> {
        User player = getItem();
        System.out.println("Thách đấu: " + player.getUsername());
        // TODO: Gửi message CHALLENGE_PLAYER đến server
         client.sendMessage(new Message(MessageType.CHALLENGE_PLAYER, player.getUsername()));
      });
    }

    @Override
    protected void updateItem(User player, boolean empty) {
      super.updateItem(player, empty);
      if (empty || player == null) {
        setGraphic(null);
      } else {
        InputStream stream = getClass().getResourceAsStream("/com/thanhthbm/cardgame/images/avatar.png");
         avatar.setImage(new Image(stream));
         // Đặt avatar
        nameLabel.setText(String.format("%s %d điểm", player.getUsername(), player.getScore()));

        if (PlayerStatus.AVAILABLE == player.getStatus()) {
          statusIndicator.setFill(Color.GREEN);
          statusLabel.setText("ON");
          challengeButton.setDisable(false);
        } else {
          statusIndicator.setFill(Color.RED);
          statusLabel.setText("OFF");
          challengeButton.setDisable(true);
        }

        setGraphic(hBox);
      }
    }

  }

  private boolean getChallengeResponse(String challengerName){
    Alert alert = new Alert(AlertType.CONFIRMATION);
    alert.setTitle("Lời mời thách đấu");
    alert.setHeaderText(challengerName + " muốn thách đấu bạn với bạn!");
    alert.setContentText("Bạn có muốn chấp nhận không? Tự động từ chối lời mời sau 10 giây nếu không lựa chọn");

    ButtonType acceptButton = new ButtonType("Chấp nhận");
    ButtonType rejectButton = new ButtonType("Từ chối");

    alert.getButtonTypes().setAll(acceptButton, rejectButton);

    PauseTransition timeout = new PauseTransition(Duration.seconds(15));

    //nếu người chơi không chọn gì trong 10 giây, tự động từ chối
    timeout.setOnFinished(event -> {
      alert.close();
    });

    timeout.play();
    alert.initOwner(lobbyPane.getScene().getWindow());

    Optional<ButtonType> result = alert.showAndWait();

    timeout.stop();

    return result.isPresent() && result.get() == acceptButton;
  }

  public void refresh(){
    client.setListener(this);

    client.sendMessage(new Message(MessageType.GET_ONLINE_LIST, null));
  }
}
