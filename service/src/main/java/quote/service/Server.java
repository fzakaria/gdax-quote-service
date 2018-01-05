package quote.service;

import com.sun.net.httpserver.HttpServer;
import java.io.Closeable;
import java.net.URI;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;

/**
 * Starts the lightweight HTTP server serving the JAX-RS application. Uses the default HTTP server
 * included in the JDK
 */
public class Server implements Closeable {

  private final HttpServer server;

  Server(int port) {
    URI baseUri = UriBuilder.fromUri("http://localhost/").port(port).build();
    server = JdkHttpServerFactory.createHttpServer(baseUri, new QuoteApplication(), false);
  }

  public int getPort() {
    return server.getAddress().getPort();
  }

  public void start() {
    server.start();
  }

  public void stop() {
    close();
  }

  @Override
  public void close() {
    server.stop(0);
  }
}
