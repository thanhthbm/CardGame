package model;

import java.io.Serializable;

public class CardUpdateInfo implements Serializable {
  private static final long serialVersionUID = 1L;

  private String username;
  private Card pickedCard;
  private int cardIndex;

  public CardUpdateInfo(String username, Card pickedCard, int cardIndex) {
    this.username = username;
    this.pickedCard = pickedCard;
    this.cardIndex = cardIndex;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public Card getPickedCard() {
    return pickedCard;
  }

  public void setPickedCard(Card pickedCard) {
    this.pickedCard = pickedCard;
  }

  public int getCardIndex() {
    return cardIndex;
  }

  public void setCardIndex(int cardIndex) {
    this.cardIndex = cardIndex;
  }
}
