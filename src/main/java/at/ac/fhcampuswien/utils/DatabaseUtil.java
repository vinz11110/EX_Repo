package at.ac.fhcampuswien.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtil {
    private static final String JDBC_URL = "jdbc:h2:~/MovieDB";
    private static final String USER = "user";
    private static final String PASSWORD = "pw";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
    }
}
