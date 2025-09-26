package com.thanhthbm.cardgame.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.application.Platform;

public class GameClient {
  private final String host;
  private final int port;
  private ClientListener listener;

  private Socket socket;
  private BufferedReader in;
  private PrintWriter out;
  private Thread readerThread;
  private final AtomicBoolean running = new AtomicBoolean(false);

  public GameClient(String host, int port, ClientListener listener) {
    this.host = host;
    this.port = port;
    this.listener = listener;
  }

  public void connect() throws IOException {
    if (running.get()) return;
    socket = new Socket(host, port);
    in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
    out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true); //set auto flush to true

    running.set(true);

    Platform.runLater(() -> listener.onConnected());

    readerThread = new Thread(() -> this.readLoop(), "cg-client-reader");

    readerThread.setDaemon(true);
    readerThread.start();
  }

  private void readLoop() {
    Exception e = null;
    try{
      String line;
      while (running.get() && (line = in.readLine()) != null) {
        final String msg = line;
        Platform.runLater(() -> listener.online(msg));
      }
    } catch (Exception ex) {
      e = ex;
    } finally {
      running.set(false);
      closeSocket();
      final Exception cause = e;
      Platform.runLater(() -> listener.onDisconnected(cause));


    }
  }

  private void closeSocket() {
    if (out != null) {
      out.flush();
    }
    if (socket != null) {
      try {
        socket.close();
      } catch (IOException e) {}
    }
    if (in != null) {
      try {
        in.close();
      } catch (IOException e) {}
    }

    out = null;
    in = null;
    socket = null;
  }


  public synchronized void close() {
    running.set(false);
    closeSocket();
  }

  public synchronized void sendLine(String line) {
    if (!running.get() || out == null) return;
    out.println(line);
  }

  public void login(String username, String password) {
    sendLine(("LOGIN " + username + " " + password));
  }

  public void register(String username, String password) {
    sendLine(("REGISTER " + username + " " + password));
  }

  public boolean isRunning() {
    return running.get();
  }

  public void setListener(ClientListener listener) {
    this.listener = listener;
  }

}
