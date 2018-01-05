package api.gdax.client;

public enum Level {

  /** Only the best bid and ask */
  ONE("1"),

  /** Top 50 bids and asks (aggregated) */
  TWO("2"),

  /** Full order book (non aggregated) */
  THREE("3");

  private final String name;

  Level(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }
}
