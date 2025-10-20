package model;

import java.io.Serializable;

public class ChangePasswordDTO implements Serializable {
  private static final long serialVersionUID = 1L;
  private String username;
  private String currentPassword;
  private String newPassword;
  private String rePassword;

  public ChangePasswordDTO() {}

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getCurrentPassword() {
    return currentPassword;
  }

  public void setCurrentPassword(String currentPassword) {
    this.currentPassword = currentPassword;
  }

  public String getNewPassword() {
    return newPassword;
  }

  public void setNewPassword(String newPassword) {
    this.newPassword = newPassword;
  }

  public String getRePassword() {
    return rePassword;
  }

  public void setRePassword(String rePassword) {
    this.rePassword = rePassword;
  }

  public ChangePasswordDTO(String username, String currentPassword, String newPassword,
      String rePassword) {
    this.username = username;
    this.currentPassword = currentPassword;
    this.newPassword = newPassword;
    this.rePassword = rePassword;
  }
}
