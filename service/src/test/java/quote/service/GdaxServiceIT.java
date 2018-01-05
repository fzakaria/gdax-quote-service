package quote.service;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Integration test since this makes external calls to GDAX Using this test bed to verify that the
 * retrofit client works as expected.
 */
public class GdaxServiceIT {

  private static final GdaxService client = GdaxService.client();

  @Test
  public void testClientWorksSimple() throws Exception {
    Call<GdaxService.OrderBook> call = client.getProductOrderBook("BTC", "USD", 1);
    Response<GdaxService.OrderBook> response = call.execute();
    Assertions.assertThat(response.raw().code()).isEqualTo(200);
  }

  @Test
  public void testClientJsonSerializesDeserializes() throws Exception {
    Call<GdaxService.OrderBook> call = client.getProductOrderBook("BTC", "USD", 1);
    Response<GdaxService.OrderBook> response = call.execute();
    Assertions.assertThat(response.raw().code()).isEqualTo(200);

    GdaxService.OrderBook orderBook = response.body();
    Assertions.assertThat(orderBook).isNotNull();
    Assertions.assertThat(orderBook.getSequence()).isNotBlank();
    Assertions.assertThat(orderBook.getAsks()).isNotEmpty();
    Assertions.assertThat(orderBook.getBids()).isNotEmpty();
  }

  @Test
  public void testNonExistentCurrencyPair() throws Exception {
    Call<GdaxService.OrderBook> call = client.getProductOrderBook("BTC", "FAKE", 1);
    Response<GdaxService.OrderBook> response = call.execute();
    Assertions.assertThat(response.raw().code()).isEqualTo(404);
    Assertions.assertThat(response.isSuccessful()).isFalse();
    Assertions.assertThat(response.errorBody().string()).contains("NotFound");
  }
}
