module com.thanhthbm.cardgame {
  requires javafx.controls;
  requires javafx.fxml;
  requires javafx.web;

  requires org.controlsfx.controls;
  requires com.dlsc.formsfx;
  requires net.synedra.validatorfx;
  requires org.kordamp.ikonli.javafx;
  requires org.kordamp.bootstrapfx.core;
  requires eu.hansolo.tilesfx;
  requires com.almasb.fxgl.all;
  requires annotations;

  // Cho phép FXMLLoader truy cập field và method trong controller
  opens com.thanhthbm.cardgame.controller to javafx.fxml;

  // Nếu bạn có FXML gắn fx:controller="com.thanhthbm.cardgame.App" thì mở cả package chính:
  opens com.thanhthbm.cardgame.fxml to javafx.fxml;

  // Các package public (App, SceneManager, controller nếu dùng từ ngoài)
  exports com.thanhthbm.cardgame;
  exports com.thanhthbm.cardgame.constants;
}
