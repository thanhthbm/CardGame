import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import model.Message;
import model.User;

public class ClientHandler extends Thread {
  private final Socket socket;
  private ObjectInputStream in;
  private ObjectOutputStream out;
  private ServerMain server;
  private MessageHandler messageHandler;
  private User user;
  private GameRoomHandler currentRoom;

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

          GameRoomHandler currentRoom = getCurrentRoom();

          if (currentRoom != null) {
            currentRoom.postPlayerAction(receivedMessage);
          } else {
            messageHandler.processMessage(receivedMessage);
          }

        } catch (ClassNotFoundException e) {
          e.printStackTrace();
          System.err.println("Received an unknown object type.");
          break;
        } catch (IOException e) {
          e.printStackTrace();
          System.out.printf("Client %s disconnected.\n", socket.getInetAddress().getHostName());
          break;
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
      System.err.println("Error setting up streams for client: " + e.getMessage());
    } finally {
      //dọn dẹp: xóa user khỏi luồng, xóa user khỏi danh sách đang online trên server
      if (this.user != null) {
        server.removeClient(user.getUsername());
      }
      try {
        //đóng luồng sau khi vòng while kết thúc
        socket.close();
        in.close();
        out.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public void sendMessage(Message message) throws IOException {
    if (out != null) {
      out.writeObject(message);
      out.flush();
    }
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public void setCurrentRoom(GameRoomHandler currentRoom) {
    this.currentRoom = currentRoom;
  }

  public GameRoomHandler getCurrentRoom() {
    return currentRoom;
  }

  public void leaveRoom() {
    this.currentRoom = null;
  }
}
