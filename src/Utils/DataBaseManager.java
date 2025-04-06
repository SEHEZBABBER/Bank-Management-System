package Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DataBaseManager {

    String url = "jdbc:mysql://localhost:3306/Bank";
    String username = "root";
    String password = "root";

    public void insertUser(String name, String email, double balance) {
        String query = "INSERT INTO users (name, email, balance) VALUES (?, ?, ?)";

        try {
            Connection conn = DriverManager.getConnection(url, username, password);
            System.out.println("Connection Successful");

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setDouble(3, balance);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Insertion Successful");
            } else {
                System.out.println("Error Inserting User");
            }

            conn.close();
        } catch (SQLException e) {
            System.out.println("Connection failed!");
            e.printStackTrace();
        }
    }
}
