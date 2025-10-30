package model.DTO;

import java.io.Serializable;

public class ChallengeResponse implements Serializable {
  private static final long serialVersionUID = 1L;
  private String responseTo;
  private boolean accepted;
  private String deckType; //FULL/SHORT

  public ChallengeResponse(String responseTo, boolean accepted,  String deckType) {
    this.responseTo = responseTo;
    this.accepted = accepted;
    this.deckType = deckType;
  }

  public String getResponseTo() {
    return responseTo;
  }

  public void setResponseTo(String responseTo) {
    this.responseTo = responseTo;
  }

  public boolean isAccepted() {
    return accepted;
  }

  public void setAccepted(boolean accepted) {
    this.accepted = accepted;
  }

  public String getDeckType() {
    return deckType;
  }

  public void setDeckType(String deckType) {
    this.deckType = deckType;
  }
}
