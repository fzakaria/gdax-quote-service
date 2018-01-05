package api.gdax.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Objects;

public class OrderBook {

  private final String sequence;
  private final List<Order> bids;
  private final List<Order> asks;

  @JsonCreator
  public OrderBook(
      @JsonProperty("sequence") String sequence,
      @JsonProperty("bids") List<Order> bids,
      @JsonProperty("asks") List<Order> asks) {
    this.sequence = sequence;
    this.bids = bids;
    this.asks = asks;
  }

  public String getSequence() {
    return sequence;
  }

  /** Orders to buy */
  public List<Order> getBids() {
    return bids;
  }

  /** Orders to sell */
  public List<Order> getAsks() {
    return asks;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    OrderBook orderBook = (OrderBook) o;
    return Objects.equals(sequence, orderBook.sequence)
        && Objects.equals(bids, orderBook.bids)
        && Objects.equals(asks, orderBook.asks);
  }

  @Override
  public int hashCode() {
    return Objects.hash(sequence, bids, asks);
  }
}
