package com.thanhthbm.cardgame.controller;

import com.thanhthbm.cardgame.AppContext;
import model.LeaderboardItem; // Ensure this is from your 'common' module
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
import model.Message; // Ensure this is from your 'common' module

import java.util.List;

public class HomeController implements ClientListener {

  private GameClient client;

  @FXML
  private ListView<model.LeaderboardItem> leaderboardList;
  private final ObservableList<model.LeaderboardItem> items = FXCollections.observableArrayList();

  @FXML
  public void initialize() {
    // 1. Set up the UI components
    leaderboardList.setItems(items);
    leaderboardList.setCellFactory(list -> new LeaderboardCell());

    // 2. Get the single, shared GameClient instance from AppContext
    this.client = AppContext.getInstance().getClient();
    // 3. Register this controller as the current listener for network events
    this.client.setListener(this);

    // 4. Send a Message to request the leaderboard data
    // This replaces the old client.sendLine("LEADERBOARD")
    client.sendMessage(new Message(Message.MessageType.GET_LEADERBOARD, null));
  }

  @Override
  public void onConnected() {
    // This is called when the connection is established.
    // We can re-request data here if needed, for example, if the connection was lost and then re-established.
    Platform.runLater(() -> {
      System.out.println("HomeView: Connection active.");
      client.sendMessage(new Message(Message.MessageType.GET_LEADERBOARD, null));
    });
  }

  @Override
  public void onDisconnected(String reason) {
    Platform.runLater(() -> {
      // Optionally, display a status to the user that the connection was lost.
      System.out.println("HomeView: Disconnected - " + reason);
    });
  }

  @Override
  public void onMessageReceived(Message message) {
    // Handle messages from the server on the JavaFX Application Thread
    Platform.runLater(() -> {
      switch (message.getType()) {
        case LEADERBOARD:
          // The payload should be a List of LeaderboardItem objects
          if (message.getPayload() instanceof List) {
            List<model.LeaderboardItem> leaderboardItems = (List<model.LeaderboardItem>) message.getPayload();
            updateLeaderboardUI(leaderboardItems);
          }
          break;

        // Handle other message types relevant to the Home screen
        // case PLAYER_JOINED_ROOM:
        //     ...
        //     break;
      }
    });
  }

  /**
   * Updates the leaderboard ListView with new data from the server.
   * This method no longer needs to parse a string.
   * @param leaderboardItems The list of items to display.
   */
  private void updateLeaderboardUI(List<LeaderboardItem> leaderboardItems) {
    this.items.setAll(leaderboardItems);
  }

  // The inner class LeaderboardCell remains unchanged as it's for UI rendering.
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
}