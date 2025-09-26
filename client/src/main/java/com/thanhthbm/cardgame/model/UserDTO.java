package com.thanhthbm.cardgame.model;

public class UserDTO {
  private int id;
  private String username;
  private int score;

  public UserDTO() {
  }

  public int getScore() {
    return score;
  }

  public void setScore(int score) {
    this.score = score;
  }

  public UserDTO(String username, int score) {
    this.username = username;
    this.score = score;

  }

  public int getId() {
    return id;
  }

  public String getUsername() {
    return username;
  }


  public void setId(int id) {
    this.id = id;
  }

  public void setUsername(String username) {
    this.username = username;
  }

}
