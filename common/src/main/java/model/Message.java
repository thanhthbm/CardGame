package model;

import java.io.Serializable;

public class Message implements Serializable {
  private static final long serialVersionUID = 1L;

  public enum MessageType {
    LOGIN, LOGOUT, REGISTER, LOGIN_FAILED, REGISTER_FAILED, LOGIN_SUCCESS, REGISTER_SUCCESS,
    LEADERBOARD_UPDATE, GET_LEADERBOARD, LEADERBOARD,
    GET_ONLINE_LIST, ONLINE_LIST,
    CHALLENGE_PLAYER, CHALLENGE_REQUEST, CHALLENGE_RESPONSE, CHALLENGE_FAILED, CHALLENGE_SUCCESS,
    GAME_START, EXIT_GAME, GAME_RESULT, GAME_FORFEIT,
    PICK_CARD, TURN_UPDATE, CARD_PICKED_UPDATE,
    WINNER,
    CHANGE_PASSWORD, CHANGE_PASSWORD_SUCCESS, CHANGE_PASSWORD_FAILED,
    GET_HISTORY_LIST, RETURN_HISTORY_LIST
  }

  private MessageType type;
  private Object payload;

  public Message(MessageType type, Object payload) {
    this.type = type;
    this.payload = payload;
  }

  public MessageType getType() {
    return type;
  }

  public void setType(MessageType type) {
    this.type = type;
  }

  public Object getPayload() {
    return payload;
  }

  public void setPayload(Object payload) {
    this.payload = payload;
  }
}
