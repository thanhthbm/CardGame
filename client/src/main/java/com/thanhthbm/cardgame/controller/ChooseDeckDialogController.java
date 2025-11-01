package com.thanhthbm.cardgame.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ChooseDeckDialogController {

    @FXML private Label titleLabel;
    @FXML private Label messageLabel;
    @FXML private Button fullButton;
    @FXML private Button shortButton;
    @FXML private Button cancelButton;

    private String result = "CANCEL";

    public void setData(String playerName) {
        messageLabel.setText("Chọn loại bộ bài để thách đấu " + playerName + ":");
    }
    public String getResult() {
        return result;
    }

    @FXML
    private void handleFull() {
        result = "FULL";
        closeStage();
    }

    @FXML
    private void handleShort() {
        result = "SHORT";
        closeStage();
    }

    @FXML
    private void handleCancel() {
        result = "CANCEL";
        closeStage();
    }

    private void closeStage() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}