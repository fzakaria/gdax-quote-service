package quote.service;

import api.gdax.client.GdaxClient;
import java.math.BigDecimal;
import javax.ws.rs.core.Application;
import org.assertj.core.api.Assertions;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;
import quote.client.ApiClient;
import quote.client.api.QuoteServiceApi;
import quote.client.model.QuoteRequest;
import quote.client.model.QuoteResponse;

public class QuoteApiIT extends JerseyTest {

  @Override
  protected Application configure() {
    return new QuoteApplication();
  }

  @Test
  public void testSimpleOrderBookHappy() throws Exception {
    QuoteServiceApi client = createClient();
    QuoteRequest request =
        new QuoteRequest()
            .action(QuoteRequest.ActionEnum.BUY)
            .quoteCurrency("USD")
            .baseCurrency("BTC")
            .amount(BigDecimal.ONE);
    QuoteResponse response = client.getQuote(request);
    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getCurrency()).isEqualTo("USD");
    Assertions.assertThat(response.getPrice()).isGreaterThan(BigDecimal.ZERO);
    Assertions.assertThat(response.getTotal()).isGreaterThan(BigDecimal.ZERO);
  }

  @Test
  public void testReverseCurrencyOrderBookHappy() throws Exception {
    QuoteServiceApi client = createClient();
    QuoteRequest request =
        new QuoteRequest()
            .action(QuoteRequest.ActionEnum.BUY)
            .quoteCurrency("BTC")
            .baseCurrency("USD")
            .amount(BigDecimal.ONE);
    QuoteResponse response = client.getQuote(request);
    Assertions.assertThat(response).isNotNull();
    Assertions.assertThat(response.getCurrency()).isEqualTo("BTC");
    Assertions.assertThat(response.getPrice()).isGreaterThan(BigDecimal.ZERO);
    Assertions.assertThat(response.getTotal()).isGreaterThan(BigDecimal.ZERO);
  }

  @Test
  public void testCurrencyPairExists() throws Exception {
    QuoteApi api = new QuoteApi(GdaxClient.defaultClient());
    Assertions.assertThat(api.hasCurrencyPair("BTC", "USD")).isTrue();
    Assertions.assertThat(api.hasCurrencyPair("USD", "BTC")).isFalse();
  }

  protected QuoteServiceApi createClient() {
    ApiClient client = new ApiClient().setBasePath(String.format("http://localhost:%s", getPort()));
    return new QuoteServiceApi(client);
  }
}
