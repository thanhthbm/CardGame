package com.thanhthbm.cardgame.constants;

import com.thanhthbm.cardgame.controller.ChooseDeckDialogController;
import com.thanhthbm.cardgame.controller.AlertController;
import com.thanhthbm.cardgame.controller.ConfirmDialogController;
import com.thanhthbm.cardgame.controller.GameResultDialogController;
// Import Controller mới
import com.thanhthbm.cardgame.controller.SimpleConfirmDialogController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import model.DTO.GameResult;

import java.io.IOException;

public class AlertFactory {

    public static void showAlert(Window owner, String title, String message) {
        try {
            FXMLLoader loader = new FXMLLoader(AlertFactory.class.getResource("/com/thanhthbm/cardgame/fxml/AlertView.fxml"));
            Parent root = loader.load();
            AlertController controller = loader.getController();
            controller.setData(title, message);
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            scene.getStylesheets().add(AlertFactory.class.getResource("/com/thanhthbm/cardgame/css/AlertView.css").toExternalForm());

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.setScene(scene);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setOpacity(0.0);

            stage.setOnShown(e -> {
                double ownerX = owner.getX();
                double ownerY = owner.getY();
                double ownerWidth = owner.getWidth();
                double ownerHeight = owner.getHeight();

                double dialogWidth = stage.getWidth();
                double dialogHeight = stage.getHeight();
                stage.setX(ownerX + (ownerWidth - dialogWidth) / 2);
                stage.setY(ownerY + (ownerHeight - dialogHeight) / 2);
                stage.setOpacity(1.0);
            });
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean showConfirmation(Window owner, String title, String message) {
        try {
            FXMLLoader loader = new FXMLLoader(AlertFactory.class.getResource("/com/thanhthbm/cardgame/fxml/SimpleConfirmDialog.fxml"));
            Parent root = loader.load();
            SimpleConfirmDialogController controller = loader.getController();
            controller.setData(title, message);

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            scene.getStylesheets().add(AlertFactory.class.getResource("/com/thanhthbm/cardgame/css/ConfirmDialog.css").toExternalForm());

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.setScene(scene);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setOpacity(0.0);

            stage.setOnShown(e -> {
                double ownerX = owner.getX();
                double ownerY = owner.getY();
                double ownerWidth = owner.getWidth();
                double ownerHeight = owner.getHeight();
                double dialogWidth = stage.getWidth();
                double dialogHeight = stage.getHeight();
                stage.setX(ownerX + (ownerWidth - dialogWidth) / 2);
                stage.setY(ownerY + (ownerHeight - dialogHeight) / 2);
                stage.setOpacity(1.0);
            });

            stage.showAndWait();
            return controller.getResult();

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void showChallengeConfirmation(Window owner, String title, String message, String challengerName, String deckType) {
        try {
            FXMLLoader loader = new FXMLLoader(AlertFactory.class.getResource("/com/thanhthbm/cardgame/fxml/ConfirmDialog.fxml"));
            Parent root = loader.load();
            ConfirmDialogController controller = loader.getController();
            controller.setData(title, message, challengerName, deckType);

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            scene.getStylesheets().add(AlertFactory.class.getResource("/com/thanhthbm/cardgame/css/ConfirmDialog.css").toExternalForm());

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.setScene(scene);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setOpacity(0.0);

            stage.setOnShown(e -> {
                double ownerX = owner.getX();
                double ownerY = owner.getY();
                double ownerWidth = owner.getWidth();
                double ownerHeight = owner.getHeight();
                double dialogWidth = stage.getWidth();
                double dialogHeight = stage.getHeight();
                stage.setX(ownerX + (ownerWidth - dialogWidth) / 2);
                stage.setY(ownerY + (ownerHeight - dialogHeight) / 2);
                stage.setOpacity(1.0);
            });
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String showDeckChoice(Window owner, String playerName) {
        try {
            FXMLLoader loader = new FXMLLoader(AlertFactory.class.getResource("/com/thanhthbm/cardgame/fxml/ChooseDeckDialog.fxml"));
            Parent root = loader.load();

            ChooseDeckDialogController controller = loader.getController();
            controller.setData(playerName);

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            scene.getStylesheets().add(AlertFactory.class.getResource("/com/thanhthbm/cardgame/css/ConfirmDialog.css").toExternalForm());

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.setScene(scene);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setOpacity(0.0);

            stage.setOnShown(e -> {
                double ownerX = owner.getX();
                double ownerY = owner.getY();
                double ownerWidth = owner.getWidth();
                double ownerHeight = owner.getHeight();
                double dialogWidth = stage.getWidth();
                double dialogHeight = stage.getHeight();
                stage.setX(ownerX + (ownerWidth - dialogWidth) / 2);
                stage.setY(ownerY + (ownerHeight - dialogHeight) / 2);
                stage.setOpacity(1.0);
            });

            stage.showAndWait();
            return controller.getResult();

        } catch (IOException e) {
            e.printStackTrace();
            return "CANCEL";
        }
    }

    public static void showGameResult(Window owner, GameResult result, String myUsername) {
        try {
            FXMLLoader loader = new FXMLLoader(AlertFactory.class.getResource("/com/thanhthbm/cardgame/fxml/GameResultDialog.fxml"));
            Parent root = loader.load();

            GameResultDialogController controller = loader.getController();
            controller.setData(result, myUsername);

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            scene.getStylesheets().add(AlertFactory.class.getResource("/com/thanhthbm/cardgame/css/GameResultDialog.css").toExternalForm());

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.setScene(scene);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setOpacity(0.0);

            stage.setOnShown(e -> {
                double ownerX = owner.getX();
                double ownerY = owner.getY();
                double ownerWidth = owner.getWidth();
                double ownerHeight = owner.getHeight();
                double dialogWidth = stage.getWidth();
                double dialogHeight = stage.getHeight();
                stage.setX(ownerX + (ownerWidth - dialogWidth) / 2);
                stage.setY(ownerY + (ownerHeight - dialogHeight) / 2);
                stage.setOpacity(1.0);
            });

            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}