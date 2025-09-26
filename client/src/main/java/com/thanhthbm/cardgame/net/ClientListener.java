package com.thanhthbm.cardgame.net;

public interface ClientListener {
  void onConnected();
  void onDisconnected(Exception e);
  void online(String line);
}
