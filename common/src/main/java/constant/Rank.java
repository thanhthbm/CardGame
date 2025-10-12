package constant;


//Số của lá bài
public enum Rank {
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
    switch(this) {
      case JACK: return 11;
      case QUEEN: return 12;
      case KING: return 13;
      default: return this.sumValue;
    }
  }
}
