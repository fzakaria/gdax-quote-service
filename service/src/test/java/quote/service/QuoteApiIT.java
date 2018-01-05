package quote.service;

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
  }

  protected QuoteServiceApi createClient() {
    ApiClient client =
        new ApiClient().setBasePath(String.format("http://localhost:%s", getPort()));
    return new QuoteServiceApi(client);
  }
}
