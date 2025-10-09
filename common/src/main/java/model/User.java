package model;

import java.io.Serializable;

public class User implements Serializable {
  private static final long serialVersionUID = 1L;
  private int id;
  private String username;
  private String password;
  private int score;

  public User() {
  }

  public int getScore() {
    return score;
  }

  public void setScore(int score) {
    this.score = score;
  }

  public User(String username, String password) {
    this.username = username;
    this.password = password;
  }

  public int getId() {
    return id;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}