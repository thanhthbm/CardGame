package com.thanhthbm.cardgame.model;

public class LeaderboardItem {
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
