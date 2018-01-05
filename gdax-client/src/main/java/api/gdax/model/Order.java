package api.gdax.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.math.BigDecimal;
import java.util.Objects;

@JsonFormat(shape = JsonFormat.Shape.ARRAY)
@JsonPropertyOrder({"price", "size", "orders"})
public class Order {
  private final BigDecimal price;
  private final BigDecimal size;
  private final long quantity;

  @JsonCreator
  public Order(
      @JsonProperty("price") BigDecimal price,
      @JsonProperty("size") BigDecimal size,
      @JsonProperty("orders") long quantity) {
    this.price = price;
    this.size = size;
    this.quantity = quantity;
  }

  /** The price of the order */
  public BigDecimal getPrice() {
    return price;
  }

  /*
   * The amount of shares for the order
   */
  public BigDecimal getSize() {
    return size;
  }

  /** The quantity of orders at this price & size */
  public long getQuantity() {
    return quantity;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Order bid = (Order) o;
    return quantity == bid.quantity
        && Objects.equals(price, bid.price)
        && Objects.equals(size, bid.size);
  }

  @Override
  public int hashCode() {
    return Objects.hash(price, size, quantity);
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("Order{");
    sb.append("price=").append(price);
    sb.append(", size=").append(size);
    sb.append(", quantity=").append(quantity);
    sb.append('}');
    return sb.toString();
  }
}
