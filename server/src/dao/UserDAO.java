package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.LeaderboardItem;
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
  public int register(User user){
    String query = "insert into users(username, password, score) values(?,?,?)";
    try(
        PreparedStatement stmt = connection.prepareStatement(query,  PreparedStatement.RETURN_GENERATED_KEYS);
        ) {

      stmt.setString(1, user.getUsername());
      stmt.setString(2, user.getPassword());
      stmt.setInt(3, 0);
      stmt.executeUpdate();
      ResultSet rs = stmt.getGeneratedKeys();
      if (rs.next()) {
        return rs.getInt(1);
      }
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }

    return -1;
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

}
