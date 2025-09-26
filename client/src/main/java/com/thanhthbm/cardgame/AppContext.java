package com.thanhthbm.cardgame;

import com.thanhthbm.cardgame.model.UserDTO;
import com.thanhthbm.cardgame.net.GameClient;

public class AppContext {
  private GameClient client;
  private UserDTO currentUserDTO;

  public GameClient getClient() {
    return client;
  }

  public void setClient(GameClient client) {
    this.client = client;
  }

  public UserDTO getCurrentUser() {
    return currentUserDTO;
  }

  public void setCurrentUser(UserDTO currentUserDTO) {
    this.currentUserDTO = currentUserDTO;
  }
}
