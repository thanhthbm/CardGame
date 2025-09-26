import dao.UserDAO;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import model.LeaderboardItem;
import model.User;

public class ClientHandler extends Thread {
  private final Socket socket;
  private final ServerMain server;
  private BufferedReader in;
  private PrintWriter out;
  private final UserDAO userDAO = new UserDAO();
  private User currentUser;
  private GameRoom joined;

  public ClientHandler(Socket socket, ServerMain server) {
    this.socket = socket;
    this.server = server;
  }

  @Override
  public void run() {
    try {
      in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
      out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true); //with auto flush = true
      out.println("HELLO|Use: LOGIN <username> <password>"); //instruct client to login

      String line;
      while ((line = in.readLine()) != null) {
        handle(line.trim());
        System.out.println(line);
      }

    }catch (IOException e) {
      e.printStackTrace();
    } finally {
      try{
        socket.close();
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }

  }

  private void handle(String s){
    String[] p = s.split("\\s+", 3); //set limit of args to 3
    if (p.length == 0) return;
    switch (p[0].toUpperCase()) {
      case "LOGIN":
        if (p.length >= 3){
          User user = new User(p[1], p[2]); //user object with username and password
          boolean ok = userDAO.checkLogin(user);

          if (ok) {
            //LOGIN_OK id username score
            out.println("LOGIN_OK " + user.getId() + " " + user.getUsername() + " " + user.getScore());
            currentUser = user;
          }
          else out.println("LOGIN_FAIL");
        } else {
          out.println("ERROR|Use: LOGIN <username> <password>");
        }
        break;

      case "REGISTER":
        if (p.length >= 3){
          User user = new User(p[1], p[2]);
          int idx = userDAO.register(user);

          if  (idx == -1) out.println("ERROR|Use: REGISTER <username> <password>");
          else out.println("REGISTER_OK|Use: Login <username> <password>");

        } else {
          out.println("ERROR|Use: REGISTER <username> <password>");
        }
        break;

      case "LEADERBOARD":
        String result = "LEADERBOARD";

        List<LeaderboardItem> items = userDAO.getLeaderboard();
        for (LeaderboardItem item : items) {
          result += " " + item.toString();
        }
        out.println(result.trim());
        System.out.println(result);
        break;
    }
  }

  public User getCurrentUser() {
    return currentUser;
  }
}
