package com.thanhthbm.cardgame.controller;

import com.thanhthbm.cardgame.SceneManager;
import com.thanhthbm.cardgame.constants.Screen;
import com.thanhthbm.cardgame.net.GameClient;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class MainController {

  @FXML
  private Button playButton;

  private GameClient gameClient;

  @FXML
  private void goToLoginScene() {
    SceneManager.switchScene(Screen.LOGIN);
  }

  public MainController() {}
}
