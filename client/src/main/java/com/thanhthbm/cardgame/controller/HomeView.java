package com.thanhthbm.cardgame.controller;

import com.thanhthbm.cardgame.AppContext;
import com.thanhthbm.cardgame.model.LeaderboardItem;
import com.thanhthbm.cardgame.net.ClientListener;
import com.thanhthbm.cardgame.net.GameClient;
import java.util.ArrayList;
import java.util.List;
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

public class HomeView implements ClientListener {
  private AppContext context;
  private GameClient client;

  @FXML
  private ListView<LeaderboardItem> leaderboardList;
  private final ObservableList<LeaderboardItem> items = FXCollections.observableArrayList();

  public void init(AppContext context) {
    this.context = context;
    if (context != null) {
      if (context.getClient() != null) {
        this.client = context.getClient();
        this.client.setListener(this);
        this.client.sendLine("LEADERBOARD");
      }
    }

  }

  @FXML
  public void initialize() {
    leaderboardList.setItems(items);
    leaderboardList.setCellFactory(list -> new LeaderboardCell());
    if (client == null) {
      client = new GameClient("localhost", 5555, this);
      try{
        client.connect();
        client.sendLine("LEADERBOARD");
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void onConnected() {
    client.sendLine("LEADERBOARD");
  }

  @Override
  public void onDisconnected(Exception e) {
    Platform.runLater(()-> System.out.println("Disconnected"));
  }

  @Override
  public void online(String line) {
    if (line.startsWith("LEADERBOARD")) {
      Platform.runLater(() -> updateLeaderboad(line));
    }

  }

  private void updateLeaderboad(String line) {
    String[] p = line.split("\\s+");

    List<LeaderboardItem> items = new ArrayList<>();
    for (int i=1; i<p.length; i++){
      String username = p[i].split("-")[0];
      int score = Integer.parseInt(p[i].split("-")[1]);
      items.add(new LeaderboardItem(username, score));
    }
    this.items.setAll(items);

  }

  private class LeaderboardCell extends ListCell<LeaderboardItem> {
    private final HBox root = new HBox();
    private final Circle avatar = new Circle(18);
    private final VBox textBox = new VBox();
    private final Label nameLabel = new Label();
    private final Label scoreLabel = new Label();
    private final Label rankLabel = new Label();

    public LeaderboardCell(){
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

    protected void updateItem(LeaderboardItem item, boolean empty) {
      super.updateItem(item, empty);
      if (empty || item == null) {
        setGraphic(null);
      } else {
        nameLabel.setText(item.getUsername());
        scoreLabel.setText(item.getScore()+" điểm");

        avatar.setRadius(18);
        avatar.setFill(Paint.valueOf("#93c5Fd"));

        rankLabel.setText(String.valueOf(getIndex() + 1));
        setGraphic(root);
      }
    }

  }
}
