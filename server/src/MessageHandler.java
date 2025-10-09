import dao.UserDAO;
import java.util.List;
import model.LeaderboardItem;
import model.Message;
import java.io.IOException;
import model.Message.MessageType;
import model.RegisterDTO;
import model.User;

/**
 * Lớp này chịu trách nhiệm xử lý logic cho các Message nhận được từ client.
 * Tách biệt logic xử lý khỏi việc quản lý kết nối.
 */
public class MessageHandler {
  private final ClientHandler clientHandler;
  private final ServerMain server;
  private final UserDAO userDAO;

  public MessageHandler(ClientHandler clientHandler, ServerMain server) {
    this.clientHandler = clientHandler;
    this.server = server;
    this.userDAO = new UserDAO();
  }

  public void processMessage(Message message) throws IOException {
    System.out.println("Received message: " + message);

    switch (message.getType()) {
      case LOGIN:
        handleLogin(message);
        break;
      case REGISTER:
        handleRegister(message);
        break;
      case LOGOUT:
        break;
      case GET_LEADERBOARD:
        handleGetLeaderboard(message);
        break;
      default:
        System.out.println("Unknown message type: " + message.getType());
    }
  }

  private void handleLogin(Message message) {
    if (message.getPayload() instanceof User) {
      User user = (User) message.getPayload();
      boolean success = this.userDAO.checkLogin(user);

      try {
        if (success) {
          clientHandler.sendMessage(new Message(Message.MessageType.LOGIN_SUCCESS, user));
        } else {
          clientHandler.sendMessage(new Message(Message.MessageType.LOGIN_FAILED, "Invalid username or password"));
        }
      } catch (IOException e) {
        System.err.println("Failed to send login response to client: " + e.getMessage());
      }
    }
  }

  private void handleRegister(Message message) {
    System.out.println("Processing registration for: " + message.getPayload());
    RegisterDTO dto = (RegisterDTO) message.getPayload();

    int result = this.userDAO.register(dto);
    if (result > 0){
      try {
        clientHandler.sendMessage(new Message(MessageType.REGISTER_SUCCESS, null));
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else {
      try {
        clientHandler.sendMessage(new Message(MessageType.REGISTER_FAILED, null));
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }

  }

  private void handleGetLeaderboard(Message message)  {
    System.out.println("Processing leaderboard for: " + message.getPayload());
    List<LeaderboardItem> leaderboard = this.userDAO.getLeaderboard();

    try {
      clientHandler.sendMessage(new Message(MessageType.LEADERBOARD, leaderboard));
    } catch (IOException e) {
      System.out.println("Error sending message to client: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
