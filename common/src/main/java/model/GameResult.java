package model;

import java.io.Serializable;
import java.util.List;

public class GameResult implements Serializable {
  private static final long serialVersionUID = 1L;
  private String winnerUsername;

  private String player1Username;
  private String player2Username;

  private List<Card> player1Hand;
  private List<Card> player2Hand;
  private int player1Score;
  private int player2Score;

  public GameResult(String winnerUsername, String p1Username, String p2Username, List<Card> p1Hand, List<Card> p2Hand, int p1Score, int p2Score) {
    this.winnerUsername = winnerUsername;
    this.player1Username = p1Username;
    this.player2Username = p2Username;
    this.player1Hand = p1Hand;
    this.player2Hand = p2Hand;
    this.player1Score = p1Score;
    this.player2Score = p2Score;
  }

  public String getWinnerUsername() {
    return winnerUsername;
  }

  public void setWinnerUsername(String winnerUsername) {
    this.winnerUsername = winnerUsername;
  }

  public String getPlayer1Username() {
    return player1Username;
  }

  public void setPlayer1Username(String player1Username) {
    this.player1Username = player1Username;
  }

  public String getPlayer2Username() {
    return player2Username;
  }

  public void setPlayer2Username(String player2Username) {
    this.player2Username = player2Username;
  }

  public List<Card> getPlayer1Hand() {
    return player1Hand;
  }

  public void setPlayer1Hand(List<Card> player1Hand) {
    this.player1Hand = player1Hand;
  }

  public List<Card> getPlayer2Hand() {
    return player2Hand;
  }

  public void setPlayer2Hand(List<Card> player2Hand) {
    this.player2Hand = player2Hand;
  }

  public int getPlayer1Score() {
    return player1Score;
  }

  public void setPlayer1Score(int player1Score) {
    this.player1Score = player1Score;
  }

  public int getPlayer2Score() {
    return player2Score;
  }

  public void setPlayer2Score(int player2Score) {
    this.player2Score = player2Score;
  }
}
