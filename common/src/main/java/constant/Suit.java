package constant;

//enum cho chất của lá bài
public enum Suit {
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
