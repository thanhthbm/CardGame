package com.thanhthbm.cardgame.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class AlertController {

    @FXML
    private Label titleLabel;

    @FXML
    private Label messageLabel;

    @FXML
    private Button okButton;
    public void setData(String title, String message) {
        titleLabel.setText(title);
        messageLabel.setText(message);
    }
    @FXML
    private void handleClose() {
        Stage stage = (Stage) okButton.getScene().getWindow();
        stage.close();
    }
}