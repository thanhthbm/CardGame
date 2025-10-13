package model;

import java.io.Serializable;

public class LeaderboardItem implements Serializable {
  private static final long serialVersionUID = 4L;
  private final String username;
  private final int score;

  public LeaderboardItem(String username, int score) {
    this.username = username;
    this.score = score;
  }

  public String toString() {
    return username + "-" + score;
  }

  public String getUsername() {
    return username;
  }

  public int getScore() {
    return score;
  }

}
