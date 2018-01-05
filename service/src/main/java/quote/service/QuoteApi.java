package quote.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import javax.validation.Valid;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.ServiceUnavailableException;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quote.service.model.QuoteRequest;
import quote.service.model.QuoteResponse;
import retrofit2.Call;

/** Implementation of the QuoteApi */
public class QuoteApi extends quote.service.api.QuoteApi {

  private static final Logger LOG = LoggerFactory.getLogger(QuoteApi.class);

  private final GdaxService gdaxClient;

  public QuoteApi(GdaxService gdaxClient) {
    this.gdaxClient = gdaxClient;
  }

  /**
   * Your service will handle requests to buy or sell a particular amount of a currency (the base
   * currency) with another currency (the quote currency). The service should use the orderbook to
   * determine the best price the user would be able to get for that request by executing trades on
   * GDAX. Note that the quantity your user enters will rarely match a quantity in the order book
   * exactly. This means your code will need to aggregate orders in the order book or use parts of
   * orders to arrive at the exact quantity, and your final quote will be a weighted average of
   * those prices.
   */
  @Override
  public Response getQuote(@Valid QuoteRequest request) {
    if (request == null) {
      throw new BadRequestException("You must provide a quote request.");
    }

    //Step 1. Collect the order book, if its anything return BadRequest with the propagated message
    String baseCurrency = request.getBaseCurrency();
    String quoteCurrency = request.getQuoteCurrency();
    BigDecimal amount = request.getAmount();
    GdaxService.OrderBook orderBook = collectOrderBook(baseCurrency, quoteCurrency);

    /**
     * For a buy action, we simply walk the sell bids in order taking as much as possible until
     * we've reached the amount. We compute the weighted average here.
     */
    BigDecimal usedQuantity = BigDecimal.ZERO;
    BigDecimal weightAveragePrice = BigDecimal.ZERO;

    //bids are ordered in descending, highest price first
    //asks are ordered in ascending, lowest price first
    //in both cases we want to iterate in order 0..N
    List<GdaxService.Item> items = getOrderBookList(request.getAction(), orderBook);
    for (int i = 0; i < items.size(); i++) {
      GdaxService.Item item = items.get(i);
      BigDecimal price = item.getPrice();
      //A item can have multiple orders for a given size/quantity
      BigDecimal totalShares = item.getSize().multiply(BigDecimal.valueOf(item.getOrders()));
      BigDecimal usedShares = amount.min(totalShares);
      //update our amount for the next iteration
      usedQuantity = usedQuantity.add(usedShares);
      weightAveragePrice = weightAveragePrice.add(price.multiply(usedShares));
      if (usedQuantity.equals(amount)) {
        break;
      }
    }
    weightAveragePrice = weightAveragePrice.divide(usedQuantity);

    QuoteResponse response =
        new QuoteResponse().currency(quoteCurrency).total(usedQuantity).price(weightAveragePrice);
    return Response.ok().entity(response).build();
  }

  private List<GdaxService.Item> getOrderBookList(
      QuoteRequest.ActionEnum action, GdaxService.OrderBook orderBook) {
    return action == QuoteRequest.ActionEnum.BUY ? orderBook.getBids() : orderBook.getAsks();
  }
  /**
   * Collect the order book from GDAX using level 2 always.
   *
   * @return the order book
   * @throws BadRequestException if currencies don't exist
   * @throws ServiceUnavailableException if gdax spits any error
   */
  private GdaxService.OrderBook collectOrderBook(String baseCurrency, String quoteCurrency) {
    try {
      Call<GdaxService.OrderBook> call =
          gdaxClient.getProductOrderBook(baseCurrency, quoteCurrency, 2);
      retrofit2.Response<GdaxService.OrderBook> response = call.execute();
      if (!response.isSuccessful()) {
        if (Response.Status.fromStatusCode(response.code()) == Response.Status.NOT_FOUND) {
          throw new BadRequestException("No order book for the provided currencies.");
        }
        throw new ServiceUnavailableException(
            "An unforeseen exception was encountered talking to gdax.");
      }
      return response.body();
    } catch (IOException e) {
      throw new BadRequestException(e);
    }
  }
}
