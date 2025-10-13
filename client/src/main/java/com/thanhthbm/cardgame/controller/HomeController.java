package com.thanhthbm.cardgame.controller;

import com.thanhthbm.cardgame.context.AppContext;
import com.thanhthbm.cardgame.constants.Screen;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import model.LeaderboardItem;
import com.thanhthbm.cardgame.net.ClientListener;
import com.thanhthbm.cardgame.net.GameClient;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import model.Message;

import java.util.List;

public class HomeController implements ClientListener {
  @FXML
  private AnchorPane homePane;

  @FXML
  private Button playButton;


  private GameClient client;

  @FXML
  private ListView<model.LeaderboardItem> leaderboardList;
  private final ObservableList<model.LeaderboardItem> items = FXCollections.observableArrayList();

  @FXML
  public void initialize() {
    leaderboardList.setItems(items);
    leaderboardList.setCellFactory(list -> new LeaderboardCell());

    this.client = AppContext.getInstance().getClient();
    this.client.setListener(this);

    client.sendMessage(new Message(Message.MessageType.GET_LEADERBOARD, null));
  }

  @Override
  public void onConnected() {
    Platform.runLater(() -> {
      System.out.println("HomeView: Connection active.");
      client.sendMessage(new Message(Message.MessageType.GET_LEADERBOARD, null));
    });
  }

  @Override
  public void onDisconnected(String reason) {
    Platform.runLater(() -> {
      System.out.println("HomeView: Disconnected - " + reason);
    });
  }

  @Override
  public void onMessageReceived(Message message) {
    //Đặt vào luồng render giao diện của javafx để không block luồng mạng
    Platform.runLater(() -> {
      switch (message.getType()) {
        case LEADERBOARD:
          if (message.getPayload() instanceof List) {
            List<model.LeaderboardItem> leaderboardItems = (List<model.LeaderboardItem>) message.getPayload();
            updateLeaderboardUI(leaderboardItems);
          }
          break;
      }
    });
  }

  @FXML
  private void onLogout(ActionEvent event) {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Xác nhận");
    alert.setContentText("Bạn có chắc chắn muốn đăng xuất?");
    alert.initOwner(homePane.getScene().getWindow());
    alert.showAndWait();
    if (alert.getResult() == ButtonType.OK) {
      Message message = new Message(Message.MessageType.LOGOUT, null);
      client.sendMessage(message);

      AppContext.getInstance().setCurrentUser(null);
      SceneManager.switchScene(Screen.LOGIN);
    }

  }


  private void updateLeaderboardUI(List<LeaderboardItem> leaderboardItems) {
    this.items.setAll(leaderboardItems);
  }

  private class LeaderboardCell extends ListCell<LeaderboardItem> {
    private final HBox root = new HBox();
    private final Circle avatar = new Circle(18);
    private final VBox textBox = new VBox();
    private final Label nameLabel = new Label();
    private final Label scoreLabel = new Label();
    private final Label rankLabel = new Label();

    public LeaderboardCell() {
      root.setSpacing(10);
      root.setAlignment(Pos.CENTER_LEFT);
      root.setPadding(new Insets(8));
      root.getChildren().addAll(avatar, textBox, new Region(), rankLabel);
      HBox.setHgrow(root.getChildren().get(2), Priority.ALWAYS);

      nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
      scoreLabel.setStyle("-fx-font-size: 12; -fx-text-fill: gray;");
      rankLabel.setStyle("-fx-background-color: lightgray; -fx-padding: 4 8; -fx-background-radius: 12;");

      textBox.getChildren().addAll(nameLabel, scoreLabel);
      textBox.setSpacing(2);

      root.setStyle("-fx-background-color: #f9fafb; -fx-background-radius: 10; -fx-border-color: #e5e7eb; -fx-border-radius: 10;");
    }

    @Override
    protected void updateItem(LeaderboardItem item, boolean empty) {
      super.updateItem(item, empty);
      if (empty || item == null) {
        setGraphic(null);
      } else {
        nameLabel.setText(item.getUsername());
        scoreLabel.setText(item.getScore() + " điểm");

        avatar.setRadius(18);
        avatar.setFill(Paint.valueOf("#93c5Fd"));

        rankLabel.setText(String.valueOf(getIndex() + 1));
        setGraphic(root);
      }
    }
  }

  @FXML
  private void onPlay(ActionEvent event) {
    SceneManager.switchScene(Screen.LOBBY);
  }
}