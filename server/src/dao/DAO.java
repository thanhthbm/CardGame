package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DAO {
  protected Connection connection;

  private static final String URL = "jdbc:mysql://localhost:3307/cardgame";
  private static final String USER = "root";
  private static final String PASS = "Viet123456";


  public DAO() {
    this.connection = getConnection();
  }

  private Connection getConnection() {
    try {
      return DriverManager.getConnection(URL, USER, PASS);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
