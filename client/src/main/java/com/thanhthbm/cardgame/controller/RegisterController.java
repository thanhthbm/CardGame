package com.thanhthbm.cardgame.controller;

import com.thanhthbm.cardgame.context.AppContext;
import com.thanhthbm.cardgame.constants.Screen;
import com.thanhthbm.cardgame.net.ClientListener;
import com.thanhthbm.cardgame.net.GameClient;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import model.Message;
import model.Message.MessageType;
import model.RegisterDTO;

public class RegisterController implements ClientListener {
  @FXML
  private AnchorPane registerPane;

  @FXML
  private Button loginButton;
  private GameClient client;
  @FXML
  private TextField usernameField;
  @FXML
  private PasswordField passwordField;

  @FXML
  private void initialize() {
    this.client = AppContext.getInstance().getClient();
    this.client.setListener(this);
  }

  @Override
  public void onConnected() {
    System.out.println("Register view connected");
  }

  @Override
  public void onDisconnected(String e) {
    System.out.println("Register view disconnected");
  }

  @Override
  public void onMessageReceived(Message message) {
    Platform.runLater(() -> {
      switch (message.getType()) {
        case REGISTER_SUCCESS:
          System.out.println("Register success");
          showAlert("Thành công", "Đăng ký tài khoản thành công!");
          usernameField.clear();
          passwordField.clear();
          break;

        case REGISTER_FAILED:
          String reason = "Đăng ký tài khoản thất bại. Vui lòng thử lại.";
          if (message.getPayload() instanceof String) {
            reason = (String) message.getPayload();
          }
          showAlert("Thất bại", reason);
          break;

        default:
          break;
      }
    });
  }


  @FXML
  private void onRegister(ActionEvent event) {
    String username = usernameField.getText().trim();
    String password = passwordField.getText().trim();

    if (username.isEmpty() || password.isEmpty()) {
      showAlert("Lỗi định dạng", "Tên đăng nhập và mật khẩu không thể để trống");
      return;
    }

    if (username.length() < 4){
      showAlert("Lỗi định dạng", "Tên đăng nhập phải dài hơn 4 ký tự");
      return;
    }

    if (password.length() < 6){
      showAlert("Lỗi mật khẩu", "Mật khẩu phải dài hơn 6 ký tự");
      return;
    }

    RegisterDTO dto = new RegisterDTO(username, password);
    Message m = new Message(MessageType.REGISTER, dto);
    client.sendMessage(m);
  }

  @FXML
  private void goToLoginScene(ActionEvent event) {
    SceneManager.switchScene(Screen.LOGIN);
  }

  private void showAlert(String title, String message) {
    Alert alert = new Alert(AlertType.INFORMATION);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.initOwner(registerPane.getScene().getWindow());
    alert.getDialogPane().lookup(".content.label").setStyle("-fx-text-fill: black;");
    alert.showAndWait();
  }
}
