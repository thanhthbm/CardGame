package com.thanhthbm.cardgame.constants;

public enum Screen {
  MAIN("MainView"),
  LOGIN("LoginView"),
  REGISTER("RegisterView"),
  LOBBY("LobbyView"),
  HOME("HomeView");

  private final String baseName;

  Screen(String baseName) {
    this.baseName = baseName;
  }

  public String getFxmlPath() {
    return String.format("/com/thanhthbm/cardgame/fxml/%s.fxml", baseName);
  }

  public String getCssPath() {
    return String.format("/com/thanhthbm/cardgame/css/%s.css", baseName);
  }
}
