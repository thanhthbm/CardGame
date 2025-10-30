package com.thanhthbm.cardgame.context;

import com.thanhthbm.cardgame.net.GameClient;
import model.DTO.GameStartInfo;
import model.User;

public class AppContext {
  private static final AppContext instance = new AppContext();

  private GameClient client;
  private User currentUser;
  private GameStartInfo startInfo;

  private AppContext() {}

  public static AppContext getInstance() {
    return instance;
  }

  public GameClient getClient() {
    if (client == null) {
      throw new IllegalStateException("GameClient has not been initialized in App.java");
    }
    return client;
  }

  public void setClient(GameClient client) {
    this.client = client;
  }

  public User getCurrentUser() {
    return currentUser;
  }

  public void setCurrentUser(User currentUserDTO) {
    this.currentUser = currentUserDTO;
  }

  public GameStartInfo getStartInfo() {
    return startInfo;
  }

  public void setStartInfo(GameStartInfo startInfo) {
    this.startInfo = startInfo;
  }
}
