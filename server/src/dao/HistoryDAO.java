package dao;

import model.History;
import model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class HistoryDAO extends DAO{
  public HistoryDAO() {}

  public ArrayList<History> getHistory(int userId){
    String query = "select * from \n" +
        "((select score_1 as your_score, score_2 as opponent_score, match_time, user_id_2 as opponent_id,  b.username as opponent_username from \n" +
        "(select * from history where user_id_1 = ?) as a join cardgame.users as b on a.user_id_2 = b.id)\n" +
        "union all\n" +
        "(select  score_2 as your_score, score_1 as opponent_score, match_time, user_id_1 as opponent_id, b.username as opponent_username from \n" +
        "(select * from history where user_id_2 = ?) as a join cardgame.users as b on a.user_id_1 = b.id)) as c\n" +
        "order by match_time desc;";
    ArrayList<History> history = new ArrayList<>();

    try (PreparedStatement ps = connection.prepareStatement(query)){
      ps.setInt(1, userId);
      ps.setInt(2, userId);
      try(ResultSet rs = ps.executeQuery();){
        while(rs.next()){
          History h = new History();
          h.setMatchTime(rs.getTimestamp("match_time"));
          h.setScore1(rs.getInt("your_score"));
          h.setScore2(rs.getInt("opponent_score"));

          User opponent = new User();
          opponent.setId(rs.getInt("opponent_id"));
          opponent.setUsername(rs.getString("opponent_username"));

          h.setPlayer2(opponent);
          history.add(h);
        }
      }

    } catch (Exception e){
      e.printStackTrace();
    }
    return history;
  }

  public boolean addHistory(History history){
    String query = "insert into history (user_id_1, user_id_2, score_1, score_2) values (?, ?, ?, ?);";

    try(PreparedStatement ps = connection.prepareStatement(query)){
      ps.setInt(1, history.getPlayer1().getId());
      ps.setInt(2, history.getPlayer2().getId());
      ps.setInt(3, history.getScore1());
      ps.setInt(4, history.getScore2());

      return ps.executeUpdate() > 0;
    } catch (Exception e){
      e.printStackTrace();
    }
    return false;
  }
}