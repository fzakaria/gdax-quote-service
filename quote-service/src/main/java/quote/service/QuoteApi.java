package quote.service;

import api.gdax.client.GdaxClient;
import api.gdax.client.Level;
import api.gdax.model.Order;
import api.gdax.model.OrderBook;
import com.google.common.annotations.VisibleForTesting;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
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

/**
 * Implementation of the QuoteApi This is a somewhat simple implementation of the API requested. It
 * is simple in that it caches the currency pairs on startup once.
 */
public class QuoteApi extends quote.service.api.QuoteApi {

  private static final Logger LOG = LoggerFactory.getLogger(QuoteApi.class);

  private final GdaxClient gdaxClient;

  public QuoteApi(GdaxClient gdaxClient) {
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

    String baseCurrency = request.getBaseCurrency();
    String quoteCurrency = request.getQuoteCurrency();
    //amount to purchase in base currency
    BigDecimal amount = request.getAmount();
    QuoteRequest.ActionEnum action = request.getAction();

    //if the reverse currency pair exists, swap the action
    final OrderBook orderBook;
    final boolean isSwapped;
    if (!hasCurrencyPair(baseCurrency, quoteCurrency)
        && hasCurrencyPair(quoteCurrency, baseCurrency)) {
      action = swapAction(action);
      orderBook = collectOrderBook(quoteCurrency, baseCurrency);
      isSwapped = true;
    } else {
      orderBook = collectOrderBook(baseCurrency, quoteCurrency);
      isSwapped = false;
    }

    /**
     * For a buy action, we simply walk the sell bids in order taking as much as possible until
     * we've reached the amount. We compute the weighted average here.
     */
    BigDecimal usedQuantity = BigDecimal.ZERO;
    BigDecimal weightAveragePrice = BigDecimal.ZERO;

    //bids are ordered in descending, highest price first
    //asks are ordered in ascending, lowest price first
    //in both cases we want to iterate in order 0..N
    List<Order> orders = getOrderBookList(action, orderBook);
    for (int i = 0; i < orders.size(); i++) {
      Order order = orders.get(i);
      BigDecimal quoteCurrencyPrice = isSwapped ? order.getPrice() : BigDecimal.ONE;
      BigDecimal baseCurrencyPrice = isSwapped ? BigDecimal.ONE : order.getPrice();
      //A item can have multiple orders for a given size/quantity
      BigDecimal totalShares = order.getSize().multiply(BigDecimal.valueOf(order.getQuantity()));
      BigDecimal purchaseableShares = amount.divide(quoteCurrencyPrice, 8, RoundingMode.HALF_UP);
      BigDecimal usedShares = purchaseableShares.min(totalShares);
      //update our amount for the next iteration
      usedQuantity = usedQuantity.add(usedShares);
      weightAveragePrice = weightAveragePrice.add(baseCurrencyPrice.multiply(quoteCurrencyPrice.multiply(usedShares)));
      if (usedQuantity.equals(purchaseableShares)) {
        break;
      }
    }
    weightAveragePrice = weightAveragePrice.divide(usedQuantity, RoundingMode.HALF_UP);

    QuoteResponse response =
        new QuoteResponse().currency(quoteCurrency).total(usedQuantity).price(weightAveragePrice);
    return Response.ok().entity(response).build();
  }

  private QuoteRequest.ActionEnum swapAction(QuoteRequest.ActionEnum action) {
    return action == QuoteRequest.ActionEnum.BUY
        ? QuoteRequest.ActionEnum.SELL
        : QuoteRequest.ActionEnum.BUY;
  }

  private List<Order> getOrderBookList(QuoteRequest.ActionEnum action, OrderBook orderBook) {
    return action == QuoteRequest.ActionEnum.BUY ? orderBook.getBids() : orderBook.getAsks();
  }
  /**
   * Collect the order book from GDAX using level 2 always.
   *
   * @return the order book
   * @throws BadRequestException if currencies don't exist
   * @throws ServiceUnavailableException if gdax spits any error
   */
  private OrderBook collectOrderBook(String baseCurrency, String quoteCurrency) {
    try {
      Call<OrderBook> call = gdaxClient.getProductOrderBook(baseCurrency, quoteCurrency, Level.TWO);
      retrofit2.Response<OrderBook> response = call.execute();
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

  /**
   * Determine if a currency pair exists by trying to fetch an OrderBook
   *
   * @throws ServiceUnavailableException if something other than 200 or 404 is returned
   */
  @VisibleForTesting
  boolean hasCurrencyPair(String baseCurrency, String quoteCurrency) {
    try {
      //we use level 1 to transmit less data
      Call<OrderBook> call = gdaxClient.getProductOrderBook(baseCurrency, quoteCurrency, Level.ONE);
      retrofit2.Response<OrderBook> response = call.execute();
      if (!response.isSuccessful()) {
        if (Response.Status.fromStatusCode(response.code()) == Response.Status.NOT_FOUND) {
          return false;
        }
        throw new ServiceUnavailableException(
            "An unforeseen exception was encountered talking to gdax.");
      }
      return true;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
