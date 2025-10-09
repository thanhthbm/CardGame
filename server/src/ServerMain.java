import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ServerMain {
  private final int port = 5555;

  public static void main(String[] args) throws Exception {
    new ServerMain().start();
  }

  public void start() throws Exception {
    ServerSocket serverSocket = new ServerSocket(port);
    System.out.println("Server started, listening on port " + port);
    while (true) {
      Socket clientSocket = serverSocket.accept();
      System.out.printf("New client connected: %s\n", clientSocket.getInetAddress().getHostName());
      new ClientHandler(clientSocket, this).start();
    }
  }


}