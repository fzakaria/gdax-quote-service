package quote.service;

import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.grizzly.http.server.CLStaticHttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;

/**
 * Starts the lightweight HTTP server serving the JAX-RS application. Uses the default HTTP server
 * included in the JDK
 */
public class Server implements Closeable {

  private final HttpServer server;
  private final int port;

  public Server(int port) {
    URI baseUri = UriBuilder.fromUri("http://localhost/").port(port).build();
    this.server = GrizzlyHttpServerFactory.createHttpServer(baseUri, new QuoteApplication(), false);
    //We sent body for GET requests
    server.getServerConfiguration().setAllowPayloadForUndefinedHttpMethods(true);

    this.port = port;

    //Add static file handling
    CLStaticHttpHandler staticHttpHandler =
        new CLStaticHttpHandler(Server.class.getClassLoader(), "static/");
    this.server.getServerConfiguration().addHttpHandler(staticHttpHandler, "/swagger");
  }

  public int getPort() {
    return port;
  }

  public void start() {
    if (server.isStarted()) {
      return;
    }
    try {
      server.start();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public void stop() {
    close();
  }

  @Override
  public void close() {
    server.shutdownNow();
  }
}
