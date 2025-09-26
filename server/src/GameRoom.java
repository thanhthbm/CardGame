import controller.RoomController;
import java.util.ArrayList;
import java.util.List;
import model.User;

public class GameRoom extends Thread{
  private final String roomId;
  private final RoomController roomController = new RoomController();
  private final List<String> messages = new ArrayList<>();
  private final List<ClientHandler> clients = new ArrayList<ClientHandler>();

  private volatile boolean running = true;

  public GameRoom(String roomId) {
    this.roomId = roomId;

    //name of the room thread
    setName("Room " + roomId);
  }

  public synchronized void join(ClientHandler client) {
    clients.add(client);
    User u = client.getCurrentUser();
    roomController.join(u);
    //send message
  }

  public synchronized void leave(ClientHandler client) {
    clients.remove(client);
    User u = client.getCurrentUser();
    roomController.leave(u);
    //send message
  }

  private synchronized void sendMessage(String s){
    for (ClientHandler c : clients) {

    }
  }

  public  String getMessage() throws InterruptedException {
      synchronized (messages){
        while (messages.isEmpty()){
          messages.wait();
        }
        return messages.remove(0);
      }
  }

  @Override
  public void run() {
    while (running) {
      try {
        String msg = getMessage();
      } catch (InterruptedException e) {
        running = false;
      }
    }
  }

}
