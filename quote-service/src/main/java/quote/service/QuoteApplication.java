package quote.service;

import static quote.service.QuoteApplication.BASE_PATH;

import api.gdax.client.GdaxClient;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.ApplicationPath;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;

/** JAX-RS Application that is used to find all resources */
@ApplicationPath(BASE_PATH)
public class QuoteApplication extends ResourceConfig {

  public static final String BASE_PATH = "/";

  public QuoteApplication() {
    //perform any dependency inversion here
    GdaxClient gdaxClient = GdaxClient.defaultClient();
    quote.service.api.QuoteApi quoteApi = new QuoteApi(gdaxClient);
    registerInstances(quoteApi);
    register(ExceptionMappers.WebApplicationExceptionMapper.class);
    register(ExceptionMappers.JsonMappingExceptionMapper.class);
    register(ExceptionMappers.ConstraintViolationExceptionMapper.class);
    register(
        new LoggingFeature(
            Logger.getLogger(LoggingFeature.DEFAULT_LOGGER_NAME),
            Level.INFO,
            LoggingFeature.Verbosity.PAYLOAD_ANY,
            1024));
    property(
        LoggingFeature.LOGGING_FEATURE_VERBOSITY_SERVER, LoggingFeature.Verbosity.PAYLOAD_TEXT);
    property(LoggingFeature.LOGGING_FEATURE_LOGGER_LEVEL_SERVER, Level.INFO);
  }
}
