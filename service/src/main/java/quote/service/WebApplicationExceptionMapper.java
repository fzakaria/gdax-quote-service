package quote.service;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class WebApplicationExceptionMapper implements ExceptionMapper<Throwable> {

  @Override
  public Response toResponse(Throwable throwable) {
    if (throwable instanceof WebApplicationException) {
      WebApplicationException webEx = (WebApplicationException) throwable;
      return Response.fromResponse(webEx.getResponse()).entity(webEx.getMessage()).build();
    }
    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
  }
}
