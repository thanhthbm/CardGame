package controller;

import dao.HistoryDAO;
import dao.UserDAO;

import java.util.ArrayList;
import java.util.List;

import model.*;

import java.io.IOException;
import model.Message.MessageType;


public class MessageHandler {
  private final ClientHandler clientHandler;
  private final ServerMain server;
  private final UserDAO userDAO;
  private final HistoryDAO historyDAO;

  public MessageHandler(ClientHandler clientHandler, ServerMain server) {
    this.clientHandler = clientHandler;
    this.server = server;
    this.userDAO = new UserDAO();
    this.historyDAO = new HistoryDAO();
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
      case CHANGE_PASSWORD:
        handleChangePassword(message);
        break;
      case GET_HISTORY_LIST:
         handleGetHistoryList(message);
         break;
      default:
        System.out.println("Unknown message type: " + message.getType());
    }
  }

  private void handleChangePassword(Message message) {
    ChangePasswordDTO changePasswordDTO = (ChangePasswordDTO) message.getPayload();
    String username = changePasswordDTO.getUsername();
    String currentPassword = changePasswordDTO.getCurrentPassword();
    String newPassword = changePasswordDTO.getNewPassword();
    String rePassword = changePasswordDTO.getRePassword();

    if (!userDAO.isPasswordvalid(username, currentPassword)) {
      Message m = new Message(MessageType.CHANGE_PASSWORD_FAILED, "Mật khẩu hiện tại không đúng");
      try {
        this.clientHandler.sendMessage(m);
      } catch (IOException e) {
        System.out.println("Failed to send change password failed message");
      }
      return;
    }

    if (!newPassword.equals(rePassword)) {
      Message m = new Message(MessageType.CHALLENGE_FAILED, "Mật khẩu mới và nhập lại mật khẩu mới không giống nhau");
      try {
        this.clientHandler.sendMessage(m);
      } catch (IOException e) {
        System.out.println("Failed to send change password failed message");
      }
      return;
    }

    boolean ok = this.userDAO.changePassword(username, newPassword);
    if (!ok) {
      Message m = new Message(MessageType.CHANGE_PASSWORD_FAILED, "Lỗi không xác định");
      try {
        this.clientHandler.sendMessage(m);
      } catch (IOException e){
        System.out.println("Failed to send change password failed message");
      }
      return;
    }

    Message m = new Message(MessageType.CHANGE_PASSWORD_SUCCESS, "Đổi mật khẩu thành công!");
    try {
      this.clientHandler.sendMessage(m);
    } catch (IOException e){
      System.out.println("Failed to send change password success message");
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

      ClientHandler loggedInUser = this.server.getClient(user.getUsername());
      if (loggedInUser != null) {
        Message m = new Message(MessageType.LOGIN_FAILED, "user này đã đăng nhập!");
        try {
          clientHandler.sendMessage(m);
        } catch (IOException e) {
          e.printStackTrace();
          System.out.println("Cannot send login message");
        }

        return;
      }

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

    if (userDAO.isUsernameExists(dto.getUsername())) {
      try {
        clientHandler.sendMessage(new Message(MessageType.REGISTER_FAILED, (String)"username đã tồn tại"));
      } catch (IOException e) {
        e.printStackTrace();
        System.out.println("Failed to send register response to client: " + e.getMessage());
      }
      return;
    }

    int result = this.userDAO.register(dto);
    if (result > 0){
      try {
        clientHandler.sendMessage(new Message(MessageType.REGISTER_SUCCESS, null));
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else {
      try {
        clientHandler.sendMessage(new Message(MessageType.REGISTER_FAILED, (String)"Đã có lỗi xảy ra"));
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

  private void handleGetHistoryList(Message message) {
    System.out.println("Processing history list for: " + message.getPayload());
    int userId = (int)message.getPayload();
    ArrayList<History> listHistory = this.historyDAO.getHistory(userId);

    try {
        clientHandler.sendMessage(new Message(MessageType.RETURN_HISTORY_LIST, listHistory));
    } catch (IOException e) {
        e.printStackTrace();
    }
  }

}
