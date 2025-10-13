import controller.ServerMain;

public class ServerApplication {

  public static void main(String[] args) {
    try {
      new ServerMain().start();
    } catch (Exception e) {
      System.err.println("Không thể khởi động server: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
