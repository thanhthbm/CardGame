import com.mysql.cj.xdevapi.Client;
import dao.UserDAO;
import java.util.List;
import model.ChallengeResponse;
import model.GameStartInfo;
import model.LeaderboardItem;
import model.Message;
import java.io.IOException;
import model.Message.MessageType;
import model.RegisterDTO;
import model.User;


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
        handleLogout(message);
        break;
      case GET_LEADERBOARD:
        handleGetLeaderboard(message);
        break;
      case GET_ONLINE_LIST:
        handleGetOnlineList();
        break;
      case CHALLENGE_PLAYER:
        handleChallengeRequest(message);
        break;
      case CHALLENGE_RESPONSE:
        handleChallengeResponse(message);
        break;
      default:
        System.out.println("Unknown message type: " + message.getType());
    }
  }

  //gửi cho client danh sách các người chơi khác đang online (trừ chính mình)
  private void handleGetOnlineList() {
    List<User> onlinePlayers = server.getOnlineUsers(clientHandler.getUser().getUsername());

    Message m = new Message(MessageType.ONLINE_LIST, onlinePlayers);
    try {
      clientHandler.sendMessage(m);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void handleLogin(Message message) {
    if (message.getPayload() instanceof User) {
      User user = (User) message.getPayload();
      boolean success = this.userDAO.checkLogin(user);

      try {
        if (success) {
          clientHandler.sendMessage(new Message(Message.MessageType.LOGIN_SUCCESS, user));
          //thêm người chơi vào luồng client handler đó
          clientHandler.setUser(user);

          //thêm người chơi vào danh sách người chơi đang online trên server
          server.addClient(user.getUsername(), this.clientHandler);
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

  private void handleLogout(Message message) {
    if (this.clientHandler.getUser() != null) {
      this.server.removeClient(clientHandler.getUser().getUsername());
      this.clientHandler.setUser(null);
    }

  }

  //gửi thông báo thách đấu cho người chơi B
  private void handleChallengeRequest(Message message) {
    if (message.getPayload() instanceof String) {
      String targetUsername = (String) message.getPayload();

      String challengerUsername = clientHandler.getUser().getUsername();

      ClientHandler targetHandler = server.getClient(targetUsername);

      if (targetHandler != null) {
        Message forwardMessage = new Message(MessageType.CHALLENGE_REQUEST, challengerUsername);

        try {
          targetHandler.sendMessage(forwardMessage);
        } catch (IOException e) {
          e.printStackTrace();
          System.out.println("Error sending challenge to: " + targetUsername);
        }
      } else {
        String reason = this.clientHandler.getUser().getUsername() + " không online hoặc đang bận.";
        Message failedMessage = new Message(MessageType.CHALLENGE_FAILED, reason);
        try {
          targetHandler.sendMessage(failedMessage);
        } catch (IOException e) {
          System.out.println("Failed to send challenge to: " + targetUsername);
          e.printStackTrace();
        }
      }
    }
  }


  private void handleChallengeResponse(Message message) {
    if (message.getPayload() instanceof ChallengeResponse) {
      ChallengeResponse challengeResponse = (ChallengeResponse) message.getPayload();
      boolean accepted =  challengeResponse.isAccepted();
      String responseTo = challengeResponse.getResponseTo(); //Đây là username của người gửi lời thách đấu
      ClientHandler acceptorHandler = this.clientHandler;
      ClientHandler challengeHandler = server.getClient(responseTo);

      if (challengeHandler == null) {
        return;
      }


      if (accepted) {
//        //Cho 2 người vào 1 phòng. Add vào danh sách phòng chung của server. Server sẽ quản lý danh sách đó
//        //gửi trước tin nhắn đồng ý để test
//        ClientHandler targetHandler = server.getClient(responseTo);
//        Message forwardMessage = new Message(MessageType.CHALLENGE_SUCCESS, this.clientHandler.getUser().getUsername() + " đã chấp nhận lời thách đấu");
//        try {
//          targetHandler.sendMessage(forwardMessage);
//        } catch (IOException e) {
//          System.out.println("Failed to send challenge to: " + responseTo);
//          e.printStackTrace();
 //       }
        GameRoomHandler newRoom = new GameRoomHandler(challengeHandler, acceptorHandler, server);
        acceptorHandler.setCurrentRoom(newRoom);
        challengeHandler.setCurrentRoom(newRoom);
        newRoom.start();
        System.out.println("Game started");

        GameStartInfo startInfo = new GameStartInfo(clientHandler.getUser().getUsername(), challengeHandler.getUser().getUsername());

        Message m = new Message(MessageType.GAME_START, startInfo);

        try{
          acceptorHandler.sendMessage(m);
          challengeHandler.sendMessage(m);
        } catch (Exception e){
          e.printStackTrace();
          System.out.println("Failed to start a game");
        }


      } else {
        // gửi tin nhắn từ chối cho người thách đấu
        String reason = "";
        ClientHandler targetHandler = server.getClient(responseTo);
        reason +=  this.clientHandler.getUser().getUsername() + " đã từ chối lời thách đấu!";
        Message failedMessage = new Message(MessageType.CHALLENGE_FAILED, reason);
        try {
          targetHandler.sendMessage(failedMessage);
        } catch (IOException e) {
          System.out.println("Failed to send challenge to: " + responseTo);
          e.printStackTrace();
        }
      }
    }
  }


}
