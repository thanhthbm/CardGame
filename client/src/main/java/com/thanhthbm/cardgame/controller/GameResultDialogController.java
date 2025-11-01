package com.thanhthbm.cardgame.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import model.DTO.GameResult;

public class GameResultDialogController {

    @FXML private Label titleLabel;
    @FXML private Label player1NameLabel;
    @FXML private Label player1ScoreLabel;
    @FXML private Label player1HandLabel;
    @FXML private Label player2NameLabel;
    @FXML private Label player2ScoreLabel;
    @FXML private Label player2HandLabel;
    @FXML private Button okButton;

    public void setData(GameResult result, String myUsername) {

        if (myUsername.equals(result.getWinnerUsername())) {
            titleLabel.setText("CHIẾN THẮNG!");
            titleLabel.getStyleClass().add("result-title-victory");
        } else {
            titleLabel.setText("THẤT BẠI!");
            titleLabel.getStyleClass().add("result-title-defeat");
        }

        player1NameLabel.setText(result.getPlayer1Username());
        player1ScoreLabel.setText(String.valueOf(result.getPlayer1Score()));
        player1HandLabel.setText(result.getPlayer1Hand().toString());

        player2NameLabel.setText(result.getPlayer2Username());
        player2ScoreLabel.setText(String.valueOf(result.getPlayer2Score()));
        player2HandLabel.setText(result.getPlayer2Hand().toString());
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) okButton.getScene().getWindow();
        stage.close();
    }
}