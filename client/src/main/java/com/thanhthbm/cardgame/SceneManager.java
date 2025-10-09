package com.thanhthbm.cardgame;

import com.thanhthbm.cardgame.constants.Screen;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager {
  private static Stage stage;

  public static void init(Stage primaryStage) {
    stage = primaryStage;
  }

  public static <T> T switchScene(Screen screen) {
    try {
      FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(screen.getFxmlPath()));
      Parent root = loader.load();

      Scene scene = new Scene(root);
      scene.getStylesheets().add(SceneManager.class.getResource(screen.getCssPath()).toExternalForm());
      stage.setScene(scene);
      return loader.getController(); // returns the controller of the new scene
    } catch (IOException e) {
      throw new RuntimeException("Failed to load scene: " + screen, e);
    }
  }

}
