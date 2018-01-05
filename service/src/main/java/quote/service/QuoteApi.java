package quote.service;

import javax.validation.Valid;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;
import service.model.QuoteRequest;

/** Implementation of the QuoteApi */
public class QuoteApi extends service.api.QuoteApi {

  @Override
  public Response getQuote(@Valid QuoteRequest request) {
    if (request == null) {
      throw new BadRequestException("You must provide a quote request.");
    }
    return Response.ok().entity("magic!").build();
  }
}
