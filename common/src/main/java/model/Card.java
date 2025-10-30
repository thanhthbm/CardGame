package model;

import constant.Rank;
import constant.Suit;
import java.io.Serializable;


public class Card implements Serializable, Comparable<Card> {
  private static final long serialVersionUID = 1L;


  private final Rank rank;
  private final Suit suit;

  public Card(Rank rank, Suit suit) {
    this.rank = rank;
    this.suit = suit;
  }

  public Rank getRank() {
    return rank;
  }

  public Suit getSuit() {
    return suit;
  }

  //sắp xếp từ bé đến lớn
  @Override
  public int compareTo(Card other) {
    int rankComparison = Integer.compare(this.rank.getCompareValue(), other.rank.getCompareValue());

    if (rankComparison != 0) {
      return rankComparison;
    } else {
      return Integer.compare(this.suit.getOrder(), other.suit.getOrder());
    }
  }

  @Override
  public String toString() {
    return rank.name() + " of " + suit.name();
  }
}
