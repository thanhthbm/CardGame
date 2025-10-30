package controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import model.DTO.Message;
import model.DTO.Message.MessageType;
import model.User;

public class ServerMain {
  private final int port = 5555;
  private final Map<String, ClientHandler> onlineClients = new ConcurrentHashMap<>();

  public void addClient(String username, ClientHandler clientHandler) {
    onlineClients.put(username, clientHandler);
    System.out.println("Client " + username + " is online.");
    sendPlayerList();
  }

  public void removeClient(String username) {
    onlineClients.remove(username);
    System.out.println("Client " + username + " is offline.");
    sendPlayerList();
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

  public List<User> getOnlineUsers(String username) {
    List<User> onlineUsers = new ArrayList<>();
    for (ClientHandler clientHandler : onlineClients.values()) {
      if (!clientHandler.getUser().getUsername().equals(username)) {
        onlineUsers.add(clientHandler.getUser());
      }
    }
    return onlineUsers;
  }

  public void sendPlayerList() {
    List<User> onlinePlayersList = new ArrayList<>();

    for (ClientHandler clientHandler : onlineClients.values()) {
      User user = clientHandler.getUser();
      if (user != null) {
        onlinePlayersList.add(user);
      }
    }

    Message m = new Message(MessageType.ONLINE_LIST, onlinePlayersList);
    for (ClientHandler clientHandler : onlineClients.values()) {
      try {
        clientHandler.sendMessage(m);
      } catch (IOException e) {
        System.out.println("Error sending online players list to player " + clientHandler.getUser().getUsername());
        e.printStackTrace();
      }
    }
  }

  public Map<String, ClientHandler> getOnlineClients(){
    return onlineClients;
  }

  public ClientHandler getClient(String targetUsername) {
    return onlineClients.get(targetUsername);
  }
}