package com.thanhthbm.cardgame.net;

import model.DTO.Message;

public interface ClientListener {
  void onConnected();
  void onDisconnected(String e);
  void onMessageReceived(Message message);
}
