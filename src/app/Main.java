package app;

import db.DBConnection;
import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        try (Connection con = DBConnection.getConnection()) {
            if (con != null && !con.isClosed()) {
                System.out.println("✅ Connected to MySQL successfully!");
            } else {
                System.out.println("⚠️ Connection object is null or closed.");
            }
        } catch (Exception e) {
            System.out.println("❌ Connection failed:");
            e.printStackTrace();
        }
    }
}