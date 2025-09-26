package com.thanhthbm.cardgame;

import com.thanhthbm.cardgame.constants.Screen;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager {
  private static Stage stage;

  public static void init(Stage primaryStage) {
    stage = primaryStage;
  }

  public static <T> T switchScene(Screen screen){
    String fxmlPath = switch (screen){
      case MAIN -> "/com/thanhthbm/cardgame/fxml/MainView.fxml";
      case GAME -> "/com/thanhthbm/cardgame/fxml/game.fxml";
      case LOGIN -> "/com/thanhthbm/cardgame/fxml/LoginView.fxml";
      case REGISTER -> "/com/thanhthbm/cardgame/fxml/RegisterView.fxml";
      case HOME -> "/com/thanhthbm/cardgame/fxml/HomeView.fxml";
    };

    String cssPath = switch (screen){
      case MAIN -> "/com/thanhthbm/cardgame/css/MainView.css";
      case GAME -> "/com/thanhthbm/cardgame/css/game.css";
      case LOGIN -> "/com/thanhthbm/cardgame/css/LoginView.css";
      case REGISTER -> "/com/thanhthbm/cardgame/css/RegisterView.css";
      case HOME -> "/com/thanhthbm/cardgame/css/HomeView.css";
    };

    try {
      FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
      Parent root = loader.load();

      Scene scene = new Scene(root);
      scene.getStylesheets().add(SceneManager.class.getResource(cssPath).toExternalForm());
      stage.setScene(scene);
      return loader.getController(); //return the controller of fxml
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

}
