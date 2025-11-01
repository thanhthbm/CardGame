package com.thanhthbm.cardgame.controller;

import com.thanhthbm.cardgame.constants.AlertFactory;
import com.thanhthbm.cardgame.context.AppContext;
import com.thanhthbm.cardgame.constants.Screen;
import com.thanhthbm.cardgame.net.ClientListener;
import com.thanhthbm.cardgame.net.GameClient;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import model.DTO.Message;
import model.DTO.Message.MessageType;
import model.User;

public class LoginController implements ClientListener {
  @FXML private AnchorPane loginPane;
  @FXML private Button loginButton;
  @FXML private TextField usernameField;
  @FXML private PasswordField passwordField;

  private GameClient client;
  private Dialog<Void> waitingDialog;

  @FXML
  private void initialize() {
    this.client = AppContext.getInstance().getClient();
    this.client.setListener(this);
  }

  @FXML
  private void onLogin(ActionEvent event) {
    String username = usernameField.getText().trim();
    String password = passwordField.getText().trim();
    if (username.isEmpty() || password.isEmpty()) {
      AlertFactory.showAlert(loginPane.getScene().getWindow(), "Lỗi Đăng nhập", "Tên đăng nhập và mật khẩu không được để trống.");
      return;
    }

    User u = new User(username, password);
    Message message = new Message(MessageType.LOGIN, u);
    client.sendMessage(message);

    loginButton.setDisable(true);
    showWaitingDialog("Đăng nhập", "Đang xác thực, vui lòng chờ...");
  }

  @Override
  public void onMessageReceived(Message message) {
    Platform.runLater(() -> {
      closeWaitingDialog();

      if (message.getType() == MessageType.LOGIN_SUCCESS) {
        AppContext.getInstance().setCurrentUser((User) message.getPayload());
        SceneManager.switchScene(Screen.HOME);
      } else if (message.getType() == MessageType.LOGIN_FAILED) {
        loginButton.setDisable(false);
        AlertFactory.showAlert(loginPane.getScene().getWindow(), "Đăng nhập thất bại", (String) message.getPayload());
      }
    });
  }

  @Override
  public void onDisconnected(String reason) {
    Platform.runLater(() -> {
      closeWaitingDialog();
      loginButton.setDisable(true);
      AlertFactory.showAlert(loginPane.getScene().getWindow(), "Mất kết nối", "Không thể kết nối đến server: " + reason);
    });
  }
  private void showWaitingDialog(String title, String content) {
    closeWaitingDialog();

    waitingDialog = new Dialog<>();
    waitingDialog.initOwner(loginPane.getScene().getWindow());
    waitingDialog.setTitle(title);
    waitingDialog.setHeaderText(content);
    waitingDialog.getDialogPane().lookup(".content.label").setStyle("-fx-text-fill: black;");

    ProgressIndicator pi = new ProgressIndicator();
    waitingDialog.getDialogPane().setContent(pi);

    waitingDialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
    Node closeButton = waitingDialog.getDialogPane().lookupButton(ButtonType.CANCEL);
    closeButton.setVisible(false);

    waitingDialog.show();
  }


  private void closeWaitingDialog() {
    if (waitingDialog != null) {
      waitingDialog.close();
      waitingDialog = null;
    }
  }


  @FXML
  private void goToRegisterScene(ActionEvent event) {
    SceneManager.switchScene(Screen.REGISTER);
  }

  private void goToLobbyScene() {
    LobbyController lobbyController = SceneManager.switchScene(Screen.LOBBY);
    if (lobbyController != null) {
      lobbyController.refresh();
    }
  }

  @Override public void onConnected() { /* ... */ }
}