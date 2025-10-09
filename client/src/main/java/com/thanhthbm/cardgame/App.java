package com.thanhthbm.cardgame;
import com.thanhthbm.cardgame.constants.Screen;
import com.thanhthbm.cardgame.net.GameClient;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage stage) throws Exception {
    GameClient gameClient = GameClient.getInstance();

    AppContext.getInstance().setClient(gameClient);

    try{
      System.out.println("Attempting to connect to the server...");
      gameClient.connect();
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("Failed to connect to the server");
    }

    SceneManager.init(stage);
    SceneManager.switchScene(Screen.MAIN);

    stage.setTitle("Card Game");
    stage.setResizable(false);
    stage.setWidth(1280);
    stage.setHeight(720);
    stage.show();
  }

  @Override
  public void stop() throws Exception {
    GameClient.getInstance().disconnect("Application closing.");
    super.stop();
  }
}
