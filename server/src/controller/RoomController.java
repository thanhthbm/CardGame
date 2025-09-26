package controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.User;

public class RoomController {
  private final Map<String, User> users = new HashMap<String, User>();

  public synchronized void join(User user) {
    users.putIfAbsent(user.getUsername(), user);
  }

  public synchronized void leave(User user) {
    users.remove(user.getUsername());
  }

  public synchronized User getUser(String username) {
    return users.get(username);
  }

  public synchronized List<User> getUsers() {
    return new ArrayList<User>(users.values());
  }
}
