package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.DTO.LeaderboardItem;
import model.DTO.RegisterDTO;
import model.User;

public class UserDAO extends DAO{
  public boolean checkLogin(User user) {
    String query = "select * from users where username = ? and password = ?";
    try (
        PreparedStatement pstmt = connection.prepareStatement(query);
        ){
      pstmt.setString(1, user.getUsername());
      pstmt.setString(2, user.getPassword());
      ResultSet rs = pstmt.executeQuery();
      if (rs.next()) {
        user.setId(rs.getInt("id"));
        user.setScore(rs.getInt("score"));
        return true;
      }

    } catch (Exception e){
      System.out.println(e.getMessage());
    }
    return false;
  }

  //this method return id of registered account
  public int register(RegisterDTO dto){
    String query = "insert into users(username, password, score) values(?,?,?)";
    try(
        PreparedStatement stmt = connection.prepareStatement(query,  PreparedStatement.RETURN_GENERATED_KEYS);
        ) {

      stmt.setString(1, dto.getUsername());
      stmt.setString(2, dto.getPassword());
      stmt.setInt(3, 0);
      stmt.executeUpdate();
      ResultSet rs = stmt.getGeneratedKeys();
      if (rs.next()) {
        return rs.getInt(1);
      }
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }

    return 0;
  }

  public int getScore(int userId){
    String query = "select score from users where id = ?";
    try (PreparedStatement stmt = connection.prepareStatement(query)){
      stmt.setInt(1, userId);

      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        return rs.getInt(1);
      }
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
    return 0;
  }

  public void addScore(int userId, int delta){
    String query = "update users set score = score + ? where id=?";
    try(PreparedStatement stmt = connection.prepareStatement(query)) {
      stmt.setInt(1, delta);
      stmt.setInt(2, userId);
      stmt.executeUpdate();
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  public List<LeaderboardItem> getLeaderboard(){
    String query = "select * from users";
    List<LeaderboardItem> leaderboard = new ArrayList<>();
    try(PreparedStatement stmt = connection.prepareStatement(query)) {
      ResultSet rs = stmt.executeQuery();
      while (rs.next()) {
        leaderboard.add(new LeaderboardItem(rs.getString("username"), rs.getInt("score")));
      }
      return leaderboard;
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
    return null;
  }

  public boolean isUsernameExists(String username) {
    String query = "SELECT 1 FROM users WHERE username = ? LIMIT 1";
    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
      pstmt.setString(1, username);
      try (ResultSet rs = pstmt.executeQuery()) {
        return rs.next();
      }
    } catch (Exception e) {
      System.out.println("Lỗi khi kiểm tra username: " + e.getMessage());
      return true;
    }
  }

  public boolean changePassword(String username, String newPassword){
    String query = "update  users set password = ? where username = ?";
    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
      pstmt.setString(1, newPassword);
      pstmt.setString(2, username);
      pstmt.executeUpdate();
      return true;

    } catch (SQLException e){
      e.printStackTrace();
      System.out.println("Failed to change password");
    }
    return false;
  }

  public boolean isPasswordvalid(String username, String password){
    String query = "Select * from users where username = ? and password = ?";

    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
      pstmt.setString(1, username);
      pstmt.setString(2, password);
      ResultSet rs = pstmt.executeQuery();
      if (rs.next()) {
        return true;
      }

    } catch (SQLException e){
      System.out.println("Failed to check valid password");
      e.printStackTrace();
    }

    return false;
  }

  public User getUserByUsername(String username) {
    String query = "Select * from users where username = ?";

    User user = null;
    try(PreparedStatement statement = connection.prepareStatement(query)){
      statement.setString(1, username);
      ResultSet rs = statement.executeQuery();
      if (rs.next()) {
        user = new User();
        user.setId(rs.getInt("id"));
        user.setScore(rs.getInt("score"));
        user.setUsername(rs.getString("username"));
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }

    return user;
  }


}
