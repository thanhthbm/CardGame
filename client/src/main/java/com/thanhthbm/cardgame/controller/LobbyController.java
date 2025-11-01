package com.thanhthbm.cardgame.controller;

import com.thanhthbm.cardgame.constants.AlertFactory;
import com.thanhthbm.cardgame.context.AppContext;
import com.thanhthbm.cardgame.constants.Screen;
import com.thanhthbm.cardgame.net.ClientListener;
import com.thanhthbm.cardgame.net.GameClient;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import model.DTO.GameStartInfo;
import model.DTO.Message;
import model.DTO.Message.MessageType;
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
  public void onConnected() { System.out.println("Lobby connected"); }

  @Override
  public void onDisconnected(String e) { System.out.println("Lobby disconnected"); }

  @Override
  public void onMessageReceived(Message message) {
    Platform.runLater(() -> {
      switch (message.getType()) {
        case ONLINE_LIST:
          // (Code của bạn không đổi)
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
            String playload = (String) message.getPayload();
            String[] p = playload.split(Pattern.quote("|"));
            String sender = p[0];
            String deckType = p[1];
            getChallengeResponse(sender, deckType);
          }
          break;
        case CHALLENGE_FAILED:
          if (message.getPayload() instanceof String) {
            String reason = (String) message.getPayload();
            AlertFactory.showAlert(lobbyPane.getScene().getWindow(), "Người chơi từ chối", reason);
          }
          break;
        case CHALLENGE_SUCCESS:
          if (message.getPayload() instanceof String) {
            String reason = (String) message.getPayload();
            AlertFactory.showAlert(lobbyPane.getScene().getWindow(), "Người chơi chấp nhận", reason);
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
      hBox.getChildren().addAll(avatar, nameLabel, spacer, statusIndicator, statusLabel, challengeButton);
      challengeButton.setOnAction(event -> {
        User player = getItem();
        System.out.println("Thách đấu: " + player.getUsername());
        String result = AlertFactory.showDeckChoice(lobbyPane.getScene().getWindow(), player.getUsername());
        switch (result) {
          case "FULL":
            client.sendMessage(new Message(MessageType.CHALLENGE_PLAYER, player.getUsername() + "|" + "FULL"));
            break;
          case "SHORT":
            client.sendMessage(new Message(MessageType.CHALLENGE_PLAYER, player.getUsername() + "|" + "SHORT"));
            break;
          case "CANCEL":
            break;
        }
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
  private void getChallengeResponse(String challengerName, String deckType){

    String title = "Lời mời thách đấu";
    String message;

    if ("FULL".equals(deckType)) {
      message = challengerName + " muốn thách đấu bạn với bộ bài 52 lá!\n\n" +
              "Tự động từ chối sau 15 giây.";
    } else {
      message = challengerName + " muốn thách đấu bạn với bộ bài 36 lá!\n\n" +
              "Tự động từ chối sau 15 giây.";
    }
    AlertFactory.showChallengeConfirmation(
            lobbyPane.getScene().getWindow(),
            title,
            message,
            challengerName,
            deckType
    );
  }

  public void refresh(){
    client.setListener(this);
    client.sendMessage(new Message(MessageType.GET_ONLINE_LIST, null));
  }
}