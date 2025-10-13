package model;

import java.io.Serializable;

public class GameStartInfo implements Serializable {
  private static final long serialVersionUID = 1L;

  private String player1Name;
  private String player2Name;

  public GameStartInfo(String player1Name, String player2Name) {
    this.player1Name = player1Name;
    this.player2Name = player2Name;
  }

  public String getPlayer1Name() {
    return player1Name;
  }

  public void setPlayer1Name(String player1Name) {
    this.player1Name = player1Name;
  }

  public String getPlayer2Name() {
    return player2Name;
  }

  public void setPlayer2Name(String player2Name) {
    this.player2Name = player2Name;
  }
}
