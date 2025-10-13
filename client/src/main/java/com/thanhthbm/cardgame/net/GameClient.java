package com.thanhthbm.cardgame.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.application.Platform;
import model.Message;

public class GameClient {
  private static final GameClient instance = new GameClient();

  public static GameClient getInstance() {
    return instance;
  }

  private final String host = "26.150.208.229";
  private final int port = 5555;
  private Socket socket;
  private ObjectInputStream in;
  private ObjectOutputStream out;

  private volatile boolean connected;

  private ClientListener clientListener;

  public void setListener(ClientListener clientListener) {
    this.clientListener = clientListener;
  }

  public void connect() {
    if (connected) return;

    try {
      socket = new Socket(host, port);
      in = new ObjectInputStream(socket.getInputStream());
      out = new ObjectOutputStream(socket.getOutputStream());
      connected = true;

      Thread readerThread = new Thread(this::listenLoop);
      readerThread.setDaemon(true);
      readerThread.start();

      if (clientListener != null) {
        clientListener.onConnected();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  private void listenLoop() {
    try {
      while (connected) {
        Message message = (Message) in.readObject();
        if (clientListener != null) {
          // Gọi trực tiếp phương thức của listener từ luồng nền
          clientListener.onMessageReceived(message);
        }
      }
    } catch (Exception e) {
      disconnect("Connection lost: " + e.getMessage());
    }
  }

  public void sendMessage(Message message) {
    if (!connected) return;
    try {
      out.writeObject(message);
      out.flush();
    } catch (IOException e) {
      disconnect("Failed to send message.");
    }
  }

  public void disconnect(String reason) {
    if (!connected) return;
    connected = false;
    try {
      if (socket != null && !socket.isClosed()) socket.close();
    } catch (IOException e) {
      // Ignore
    }
    if (clientListener != null) {
      clientListener.onDisconnected(reason);
    }
  }


}
