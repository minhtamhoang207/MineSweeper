package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static DBConnection instance;
    private Connection connection;

    public static DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    private DBConnection() {}
    
    public Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String jdbcURL = "jdbc:mysql://localhost:3306/ltm_ptit?allowPublicKeyRetrieval=true&useSSL=false";
            String jdbcUsername = "root";
            String jdbcPassword = "dhh13072001";
            connection = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
            System.out.println("Connected to Database.");
        } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
        }
        return connection;
    }
}
