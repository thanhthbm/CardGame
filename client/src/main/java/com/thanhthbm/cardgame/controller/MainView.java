package com.thanhthbm.cardgame.controller;

import com.thanhthbm.cardgame.SceneManager;
import com.thanhthbm.cardgame.constants.Screen;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class MainView {

  @FXML
  private Button playButton;

  @FXML
  private void goToLoginScene() {
    SceneManager.switchScene(Screen.LOGIN);
  }

  public MainView() {}
}
