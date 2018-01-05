package quote.service;

public class Main {

  public static void main(String[] args) {
    Server server = new Server(8888);
    Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
    server.start();
  }
}
