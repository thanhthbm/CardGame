package com.thanhthbm.cardgame;

import com.thanhthbm.cardgame.constants.Screen;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class App extends Application {

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage stage) throws Exception {
    SceneManager.init(stage);
    SceneManager.switchScene(Screen.MAIN);

    stage.setTitle("Card Game");
    stage.setResizable(false);
    stage.setWidth(1280);
    stage.setHeight(720);
    stage.show();
  }
}
