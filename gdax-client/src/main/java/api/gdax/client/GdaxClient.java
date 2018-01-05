package api.gdax.client;

import api.gdax.model.OrderBook;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;

/**
 * A minimal retrofit service to communicate with GDAX for the purpose of this assignment. The main
 * API that will be used is https://docs.gdax.com/#get-product-order-book
 */
public interface GdaxClient {

  /**
   * Create a HTTP client to https://api.gdax.com Uses Jackson & Retrofit/OkHttp underneath the
   * covers
   */
  static GdaxClient defaultClient() {
    Retrofit retrofit =
        new Retrofit.Builder()
            .baseUrl("https://api.gdax.com")
            .addConverterFactory(JacksonConverterFactory.create())
            .build();
    return retrofit.create(GdaxClient.class);
  }

  @GET("/products/{baseCurrency}-{quoteCurrency}/book")
  Call<OrderBook> getProductOrderBook(
      @Path("baseCurrency") String baseCurrency,
      @Path("quoteCurrency") String quoteCurrency,
      @Query("level") Level level);

  @GET("/products")
  Call<List<Product>> getProducts();
}
