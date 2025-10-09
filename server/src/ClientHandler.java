import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import model.Message;

public class ClientHandler extends Thread {
  private final Socket socket;
  private ObjectInputStream in;
  private ObjectOutputStream out;
  private ServerMain server;
  private MessageHandler messageHandler;

  public ClientHandler(Socket socket, ServerMain server) {
    this.socket = socket;
    this.server = server;
    this.messageHandler = new MessageHandler(this, server);
  }

  @Override
  public void run() {
    try {
      out = new ObjectOutputStream(socket.getOutputStream());
      in = new ObjectInputStream(socket.getInputStream());

      while (true) {
        try {
          Message receivedMessage = (Message) in.readObject();
          messageHandler.processMessage(receivedMessage);
        } catch (ClassNotFoundException e) {
          e.printStackTrace();
          System.err.println("Received an unknown object type.");
          break;
        } catch (IOException e) {
          e.printStackTrace();
          System.out.printf("Client %s disconnected.\n", socket.getInetAddress().getHostName());
          break; // Exit the loop
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
      System.err.println("Error setting up streams for client: " + e.getMessage());
    }
  }

  public void sendMessage(Message message) throws IOException {
    if (out != null) {
      out.writeObject(message);
      out.flush();
    }
  }

}
