package com.thanhthbm.cardgame.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class SimpleConfirmDialogController {

    @FXML private Label titleLabel;
    @FXML private Label messageLabel;
    @FXML private Button confirmButton;
    @FXML private Button cancelButton;

    private boolean result = false;
    public void setData(String title, String message) {
        titleLabel.setText(title);
        messageLabel.setText(message);
    }
    public boolean getResult() {
        return result;
    }

    @FXML
    private void handleConfirm() {
        result = true;
        closeStage();
    }

    @FXML
    private void handleCancel() {
        result = false;
        closeStage();
    }

    private void closeStage() {
        Stage stage = (Stage) confirmButton.getScene().getWindow();
        stage.close();
    }
}