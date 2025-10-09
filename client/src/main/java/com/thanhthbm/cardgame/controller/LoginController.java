package com.thanhthbm.cardgame.controller;

import com.thanhthbm.cardgame.AppContext;
import com.thanhthbm.cardgame.SceneManager;
import com.thanhthbm.cardgame.constants.Screen;
import com.thanhthbm.cardgame.net.ClientListener;
import com.thanhthbm.cardgame.net.GameClient;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.Message;
import model.Message.MessageType;
import model.User;

public class LoginController implements ClientListener {
  @FXML private Button loginButton;
  @FXML private Button registerButton;
  @FXML private TextField usernameField;
  @FXML private PasswordField passwordField;
  @FXML private Label loginLabel;
  @FXML private Label usernameLabel;
  @FXML private Label passwordLabel;
  @FXML private Label statusLabel;

  private GameClient client;

  @FXML
  private void initialize() {
    this.client = AppContext.getInstance().getClient();
    this.client.setListener(this);
  }

  @FXML
  private void goToRegisterScene(ActionEvent event) {
    SceneManager.switchScene(Screen.REGISTER);
  }

  @FXML
  private void onLogin(ActionEvent event) {
    String username = usernameField.getText().trim();
    String password = passwordField.getText().trim();
    if (username.isEmpty() || password.isEmpty()) {
      setStatus("Username and password are empty");
      return;
    }

    User u = new User(username, password);

    Message message = new Message(MessageType.LOGIN, u);
    client.sendMessage(message);

    loginButton.setDisable(true);
    setStatus("Logging in...");
  }

  @Override
  public void onConnected() {
    setStatus("Connected to server");
    Platform.runLater(() -> loginButton.setDisable(false));
  }


  @Override
  public void onMessageReceived(Message message) {
    Platform.runLater(() -> {
      if (message.getType() == MessageType.LOGIN_SUCCESS) {
        goToHomeScene();
      } else if (message.getType() == MessageType.LOGIN_FAILED) {
        loginButton.setDisable(false);
        setStatus("Login failed");
      }
    });
  }

  @Override
  public void onDisconnected(String e) {
    setStatus("Disconnected from server" + (e != null ? e : ""));
    Platform.runLater(() -> loginButton.setDisable(true));
  }



  private void setStatus(String msg){
    System.out.printf("LoginView: " + msg);
    statusLabel.setText(msg);
  }

  private void goToHomeScene() {
    SceneManager.switchScene(Screen.HOME);
  }
}
