package com.thanhthbm.cardgame.controller;

import com.thanhthbm.cardgame.context.AppContext;
import com.thanhthbm.cardgame.net.GameClient;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.DTO.ChallengeResponse;
import model.DTO.Message;
import model.DTO.Message.MessageType;

public class ConfirmDialogController {

    @FXML private Label titleLabel;
    @FXML private Label messageLabel;
    @FXML private Button confirmButton;
    @FXML private Button cancelButton;

    private GameClient client;
    private String challengerName;
    private String deckType;

    private Timeline countdownTimeline;
    private boolean responseSent = false;

    @FXML
    private void initialize() {
        this.client = AppContext.getInstance().getClient();
    }

    public void setData(String title, String message, String challengerName, String deckType) {
        titleLabel.setText(title);
        messageLabel.setText(message);
        this.challengerName = challengerName;
        this.deckType = deckType;
        startTimer(15);
    }
    private void startTimer(int seconds) {
        countdownTimeline = new Timeline(new KeyFrame(Duration.seconds(seconds), event -> {
            handleCancel();
        }));
        countdownTimeline.setCycleCount(1);
        countdownTimeline.play();
    }
    private void stopTimer() {
        if (countdownTimeline != null) {
            countdownTimeline.stop();
        }
    }

    @FXML
    private void handleConfirm() {
        stopTimer();
        sendChallengeResponse(true);
        closeStage();
    }

    @FXML
    private void handleCancel() {
        stopTimer();
        sendChallengeResponse(false);
        closeStage();
    }
    private void sendChallengeResponse(boolean accepted) {
        if (responseSent) return;

        responseSent = true;
        ChallengeResponse response = new ChallengeResponse(challengerName, accepted, deckType);
        Message m = new Message(MessageType.CHALLENGE_RESPONSE, response);
        client.sendMessage(m);
    }

    private void closeStage() {
        Stage stage = (Stage) confirmButton.getScene().getWindow();
        stage.close();
    }
}