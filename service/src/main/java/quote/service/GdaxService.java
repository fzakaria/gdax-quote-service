package quote.service;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * A minimal retrofit service to communicate with GDAX for the purpose of this assignment. The main
 * API that will be used is https://docs.gdax.com/#get-product-order-book
 */
public interface GdaxService {

  /**
   * Create a HTTP client to https://api.gdax.com Uses Jackson & Retrofit/OkHttp underneath the
   * covers
   */
  static GdaxService client() {
    Retrofit retrofit =
        new Retrofit.Builder()
            .baseUrl("https://api.gdax.com")
            .addConverterFactory(JacksonConverterFactory.create())
            .build();
    return retrofit.create(GdaxService.class);
  }

  @JsonFormat(shape = JsonFormat.Shape.ARRAY)
  @JsonPropertyOrder({"price", "size", "orders"})
  class Item {
    private final BigDecimal price;
    private final BigDecimal size;
    private final long orders;

    @JsonCreator
    public Item(
        @JsonProperty("price") BigDecimal price,
        @JsonProperty("size") BigDecimal size,
        @JsonProperty("orders") long orders) {
      this.price = price;
      this.size = size;
      this.orders = orders;
    }

    public BigDecimal getPrice() {
      return price;
    }

    public BigDecimal getSize() {
      return size;
    }

    public long getOrders() {
      return orders;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Item bid = (Item) o;
      return orders == bid.orders
          && Objects.equals(price, bid.price)
          && Objects.equals(size, bid.size);
    }

    @Override
    public int hashCode() {
      return Objects.hash(price, size, orders);
    }
  }

  class OrderBook {

    private final String sequence;
    private final List<Item> bids;
    private final List<Item> asks;

    @JsonCreator
    public OrderBook(
        @JsonProperty("sequence") String sequence,
        @JsonProperty("bids") List<Item> bids,
        @JsonProperty("asks") List<Item> asks) {
      this.sequence = sequence;
      this.bids = bids;
      this.asks = asks;
    }

    public String getSequence() {
      return sequence;
    }

    public List<Item> getBids() {
      return bids;
    }

    public List<Item> getAsks() {
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

  @GET("/products/{baseCurrency}-{quoteCurrency}/book")
  Call<OrderBook> getProductOrderBook(
      @Path("baseCurrency") String baseCurrency,
      @Path("quoteCurrency") String quoteCurrency,
      @Query("level") int level);
}
