package com.thanhthbm.cardgame.controller;

import com.thanhthbm.cardgame.AppContext;
import com.thanhthbm.cardgame.SceneManager;
import com.thanhthbm.cardgame.constants.Screen;
import com.thanhthbm.cardgame.model.UserDTO;
import com.thanhthbm.cardgame.net.ClientListener;
import com.thanhthbm.cardgame.net.GameClient;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginView implements ClientListener {
  @FXML private Button loginButton;
  @FXML private Button registerButton;
  @FXML private TextField usernameField;
  @FXML private PasswordField passwordField;
  @FXML private Label loginLabel;
  @FXML private Label usernameLabel;
  @FXML private Label passwordLabel;
  @FXML private Label statusLabel;

  private GameClient client;
  private final AppContext context = new AppContext();

  @FXML
  private void initialize() {
    client = new GameClient("localhost", 5555, this);
    try {
      client.connect();
    } catch (Exception e) {
      setStatus("Failed to connect to server");
    }
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

    client.login(username, password);
    loginButton.setDisable(true);
    setStatus("Logging in...");
  }

  @Override
  public void onConnected() {
    setStatus("Connected to server");
    Platform.runLater(() -> loginButton.setDisable(false));
  }

  @Override
  public void onDisconnected(Exception e) {
    setStatus("Disconnected from server" + (e.getMessage() != null ? e.getMessage() : ""));
    Platform.runLater(() -> loginButton.setDisable(true));
  }

  @Override
  public void online(String line) {
    System.out.printf("Online: %s\n", line);

    Platform.runLater(() -> {
      if (line.startsWith("LOGIN_OK")) {
        setStatus("Logged in successfully");

        String[] p = line.split("\\s+");

        context.setClient(client);
        UserDTO userDTO = new UserDTO();
        userDTO.setId(Integer.parseInt(p[1]));
        userDTO.setUsername(p[2]);
        userDTO.setScore(Integer.parseInt(p[3]));
        context.setCurrentUser(userDTO);

        HomeView home = SceneManager.switchScene(Screen.HOME);
        home.init(context);
      } else if (line.startsWith("LOGIN_FAIL")) {
        setStatus("Logged in failed");
        loginButton.setDisable(false);
      } else{
        setStatus("Message: " + line);
      }
    });
  }

  private void setStatus(String msg){
    System.out.printf("LoginView: " + msg);
    statusLabel.setText(msg);
  }
}
