package quote.service;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import javax.ws.rs.ext.RuntimeDelegate;

/**
 * Starts the lightweight HTTP server serving the JAX-RS application. Uses the default HTTP server
 * included in the JDK
 */
public class Server implements Closeable {

  private final HttpServer server;

  public Server(int port) {
    try {
      server = HttpServer.create(new InetSocketAddress(port), 0);
      // create a handler wrapping the JAX-RS application
      HttpHandler handler =
          RuntimeDelegate.getInstance().createEndpoint(new QuoteApplication(), HttpHandler.class);

      // map JAX-RS handler to the server root
      server.createContext("/", handler);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
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
