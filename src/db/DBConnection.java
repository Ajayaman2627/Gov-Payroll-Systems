package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL =
    "jdbc:mysql://localhost:3306/ge_payroll?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC";

    private static final String USER = "payroll_app";
    private static final String PASS = "payroll123";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
