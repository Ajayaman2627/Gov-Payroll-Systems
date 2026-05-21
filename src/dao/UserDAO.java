package dao;

import db.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDAO {

    public static class LoginResult {
        public final int userId;
        public final String username;
        public final String roleName;

        public LoginResult(int userId, String username, String roleName) {
            this.userId = userId;
            this.username = username;
            this.roleName = roleName;
        }
    }

    public LoginResult login(String username, String passwordHash) {
        String sql = """
            SELECT ua.user_id, ua.username, r.role_name
            FROM user_account ua
            JOIN role r ON r.role_id = ua.role_id
            WHERE ua.username = ? AND ua.password_hash = ? AND ua.is_active = 1
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, passwordHash);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new LoginResult(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("role_name")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
