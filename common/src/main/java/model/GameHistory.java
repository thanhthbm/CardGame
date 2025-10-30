package model;

import java.time.Instant;

public class GameHistory {
  private int id;
  private User winner;
  private User loser;
  private Instant createdAt = Instant.now();
  private int winnerScore;
  private int loserScore;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public User getWinner() {
    return winner;
  }

  public void setWinner(User winner) {
    this.winner = winner;
  }

  public User getLoser() {
    return loser;
  }

  public void setLoser(User loser) {
    this.loser = loser;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }


  public int getWinnerScore() {
    return winnerScore;
  }

  public void setWinnerScore(int winnerScore) {
    this.winnerScore = winnerScore;
  }

  public int getLoserScore() {
    return loserScore;
  }

  public void setLoserScore(int loserScore) {
    this.loserScore = loserScore;
  }
}
