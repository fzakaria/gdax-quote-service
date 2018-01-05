package quote.service;

import javax.validation.Valid;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;
import quote.service.model.QuoteRequest;

/** Implementation of the QuoteApi */
public class QuoteApi extends quote.service.api.QuoteApi {

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
    return Response.ok().entity("magic!").build();
  }
}
