package quote.service;

import static quote.service.QuoteApplication.BASE_PATH;

import javax.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;

/** JAX-RS Application that is used to find all resources */
@ApplicationPath(BASE_PATH)
public class QuoteApplication extends ResourceConfig {

  public static final String BASE_PATH = "/";

  public QuoteApplication() {
    //perform any dependency inversion here
    GdaxService gdaxClient = GdaxService.client();
    quote.service.api.QuoteApi quoteApi = new QuoteApi(gdaxClient);
    registerInstances(quoteApi);

    register(WebApplicationExceptionMapper.class);
  }
}
