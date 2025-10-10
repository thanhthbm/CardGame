package model;

import java.io.Serializable;

// Enum cho Chất bài (Suit)
enum Suit {
  SPADES(1, "Bích"),
  CLUBS(2, "Tép"),
  DIAMONDS(3, "Rô"),
  HEARTS(4, "Cơ");

  private final int order;
  private final String name;

  Suit(int order, String name) {
    this.order = order;
    this.name = name;
  }

  public int getOrder() {
    return order;
  }
}

enum Rank {
  TWO("2", 2, 2),
  THREE("3", 3, 3),
  FOUR("4", 4, 4),
  FIVE("5", 5, 5),
  SIX("6", 6, 6),
  SEVEN("7", 7, 7),
  EIGHT("8", 8, 8),
  NINE("9", 9, 9),
  TEN("10", 10, 10),
  JACK("J", 11, 11), // Giá trị so sánh là 11, giá trị tính tổng là 1
  QUEEN("Q", 12, 12), // Giá trị so sánh là 12, giá trị tính tổng là 1
  KING("K", 13, 13), // Giá trị so sánh là 13, giá trị tính tổng là 1
  ACE("A", 14, 1);  // Át là lớn nhất khi so sánh (14),

  private final String displayName;
  private final int compareValue; // Giá trị để so sánh (Át lớn nhất)
  private final int sumValue;    // Giá trị để tính tổng điểm (Át là 1)

  Rank(String displayName, int compareValue, int sumValue) {
    this.displayName = displayName;
    this.compareValue = compareValue;
    this.sumValue = sumValue;
  }

  public int getCompareValue() {
    return compareValue;
  }

  public int getSumValue() {
    // Luật chơi của bạn có chút mâu thuẫn giữa điểm tính tổng và điểm so sánh,
    // đây là cách để xử lý cả hai. Dựa trên luật "tổng điểm 3 lá bài chia lấy dư cho 10",
    // J, Q, K sẽ là 11, 12, 13.
    switch(this) {
      case JACK: return 11;
      case QUEEN: return 12;
      case KING: return 13;
      default: return this.sumValue;
    }
  }
}

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
