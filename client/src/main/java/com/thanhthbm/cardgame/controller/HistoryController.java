package com.thanhthbm.cardgame.controller;

import com.thanhthbm.cardgame.constants.Screen;
import com.thanhthbm.cardgame.context.AppContext;
import com.thanhthbm.cardgame.net.ClientListener;
import com.thanhthbm.cardgame.net.GameClient;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import model.*;
import model.Message;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class HistoryController implements ClientListener {
    @FXML private ListView<History> historyListView;
    @FXML private Button backBtn;

    private GameClient client;
    private ObservableList<History> historyList = FXCollections.observableArrayList();

    public void initialize(){
        this.client = AppContext.getInstance().getClient();
        client.setListener(this);

        historyListView.setItems(historyList);
        historyListView.setCellFactory(listView -> new HistoryListCell());

        Message m = new Message(Message.MessageType.GET_HISTORY_LIST, AppContext.getInstance().getCurrentUser().getId());
        client.sendMessage(m);
    }

    @Override
    public void onConnected() {
        System.out.println("History connected");
    }

    @Override
    public void onDisconnected(String e) {
        System.out.println("History disconnected");
    }

    @Override
    public void onMessageReceived(Message message){
        Platform.runLater(()->{
           switch (message.getType()){
               case RETURN_HISTORY_LIST:
                   if(message.getPayload() instanceof ArrayList){
                       ArrayList<History> history = (ArrayList<History>) message.getPayload();
                       historyList.clear();
                       historyList.addAll(history);
                   }
                   break;
           }
        });
    }


    public void onBack(ActionEvent actionEvent) {
        SceneManager.switchScene(Screen.HOME);
    }

    private class HistoryListCell extends ListCell<History> {

        private VBox rootNode;
        private Label player1Name;
        private Label player2Name;
        private Label scoreLabel;
        private Label matchDate;
        private Region spacer;

        private User currentUser = AppContext.getInstance().getCurrentUser();

        private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");

        public HistoryListCell() {
            super();

            player1Name = new Label();
            player2Name = new Label();
            scoreLabel = new Label();
            matchDate = new Label();
            spacer = new Region();

            player1Name.setFont(new Font("System Bold", 14.0));
            player2Name.setFont(new Font("System Bold", 14.0));
            scoreLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
            matchDate.setStyle("-fx-font-size: 12px; -fx-font-style: italic;");

            HBox nameRow = new HBox(5.0, player1Name, new Label("vs"), player2Name);
            nameRow.setAlignment(Pos.CENTER_LEFT);

            HBox.setHgrow(spacer, Priority.ALWAYS);
            HBox scoreRow = new HBox(scoreLabel, spacer, matchDate);
            scoreRow.setAlignment(Pos.CENTER_LEFT);

            rootNode = new VBox(5.0, nameRow, scoreRow);
            rootNode.setPadding(new Insets(8, 12, 8, 12));
        }

        @Override
        protected void updateItem(History history, boolean empty) {
            super.updateItem(history, empty);

            if (empty || history == null || currentUser == null) {
                setGraphic(null);
            } else {
                player1Name.setText(currentUser.getUsername());

                User opponent = history.getPlayer2();
                if (opponent != null) {
                    player2Name.setText(opponent.getUsername());
                }

                scoreLabel.setText(String.format("%d - %d", history.getScore1(), history.getScore2()));

                if (history.getMatchTime() != null) {
                    matchDate.setText(dateFormat.format(history.getMatchTime()));
                }

                if (history.getScore1() > history.getScore2()) {
                    scoreLabel.setTextFill(Color.GREEN);
                    player1Name.setTextFill(Color.GREEN);
                    player2Name.setTextFill(Color.RED);
                } else if (history.getScore1() < history.getScore2()) {
                    scoreLabel.setTextFill(Color.RED);
                    player1Name.setTextFill(Color.RED);
                    player2Name.setTextFill(Color.GREEN);
                } else {
                    scoreLabel.setTextFill(Color.GRAY);
                    player1Name.setTextFill(Color.BLACK);
                    player2Name.setTextFill(Color.BLACK);
                }

                setGraphic(rootNode);
            }
        }
    }
}
