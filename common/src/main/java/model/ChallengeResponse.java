package model;

import java.io.Serializable;

public class ChallengeResponse implements Serializable {
  private static final long serialVersionUID = 1L;
  private String responseTo;
  private boolean accepted;

  public ChallengeResponse(String responseTo, boolean accepted) {
    this.responseTo = responseTo;
    this.accepted = accepted;
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
}
