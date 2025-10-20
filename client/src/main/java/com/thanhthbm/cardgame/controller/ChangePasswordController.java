package com.thanhthbm.cardgame.controller;

import com.thanhthbm.cardgame.constants.Screen;
import com.thanhthbm.cardgame.context.AppContext;
import com.thanhthbm.cardgame.net.ClientListener;
import com.thanhthbm.cardgame.net.GameClient;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.AnchorPane;
import model.ChangePasswordDTO;
import model.Message;
import model.Message.MessageType;

public class ChangePasswordController implements ClientListener {
  private GameClient client;

  @FXML
  private AnchorPane changePasswordPane;
  @FXML
  private PasswordField currentPasswordField;
  @FXML
  private PasswordField newPasswordField;
  @FXML
  private PasswordField rePasswordField;

  @FXML
  private void initialize(){
    this.client = AppContext.getInstance().getClient();
    client.setListener(this);
  }

  @Override
  public void onConnected() {
    System.out.println("Change password scene");
  }

  @Override
  public void onDisconnected(String e) {
    System.out.println("Exit change password scene");
  }

  @Override
  public void onMessageReceived(Message message) {
    Platform.runLater(() -> {
      switch (message.getType()) {
        case CHANGE_PASSWORD_SUCCESS:
          showSuccessAlert((String)message.getPayload());
          break;
        case CHANGE_PASSWORD_FAILED:
          showErrorAlert((String)message.getPayload());
          break;
        default:
          break;
      }
    });
  }

  @FXML
  private void onChangePassword(ActionEvent event) {
    String currentPassword = currentPasswordField.getText();
    String newPassword = newPasswordField.getText();
    String rePassword = rePasswordField.getText();

    if (currentPassword.length() < 6 || newPassword.length() < 6 || rePassword.length() < 6 ){
      showErrorAlert("Vui lòng kiểm tra lại thông tin: độ dài mật khẩu phải lớn hơn hoặc bằng 6!");
      return;
    }

    if (!newPassword.equals(rePassword)){
      showErrorAlert("Vui lòng nhập lại mật khẩu trùng với mật khẩu mới");
      return;
    }

    ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO();
    changePasswordDTO.setNewPassword(newPassword);
    changePasswordDTO.setRePassword(rePassword);
    changePasswordDTO.setUsername(AppContext.getInstance().getCurrentUser().getUsername());
    changePasswordDTO.setCurrentPassword(currentPassword);

    Message m = new Message(MessageType.CHANGE_PASSWORD, changePasswordDTO);

    this.client.sendMessage(m);
  }

  @FXML
  private void onBack(ActionEvent event) {
    SceneManager.switchScene(Screen.HOME);
  }

  private void showSuccessAlert(String reason){
    Alert alert = new Alert(AlertType.INFORMATION);
    alert.setTitle("Thành công");
    alert.setHeaderText(null);
    alert.setContentText(reason);
    alert.initOwner(changePasswordPane.getScene().getWindow());
    alert.showAndWait();
  }

  private void showErrorAlert(String reason){
    Alert alert = new Alert(AlertType.ERROR);
    alert.setTitle("Error");
    alert.setHeaderText(null);
    alert.setContentText(reason);
    alert.initOwner(changePasswordPane.getScene().getWindow());
    alert.showAndWait();
  }
}


