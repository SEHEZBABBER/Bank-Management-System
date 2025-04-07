package Utils;

import java.sql.*;

public class DataBaseManager {
    public static Connection ConnectToDataBase(){
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bank", "root", "root");
            return conn;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
