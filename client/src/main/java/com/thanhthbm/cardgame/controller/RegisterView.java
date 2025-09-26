package com.thanhthbm.cardgame.controller;

import com.thanhthbm.cardgame.SceneManager;
import com.thanhthbm.cardgame.constants.Screen;
import com.thanhthbm.cardgame.net.ClientListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class RegisterView implements ClientListener {

  @Override
  public void onConnected() {

  }

  @Override
  public void onDisconnected(Exception e) {

  }

  @Override
  public void online(String line) {

  }

  @FXML
  private Button loginButton;

  @FXML
  private void goToLoginScene(ActionEvent event) {
    SceneManager.switchScene(Screen.LOGIN);
  }
}
