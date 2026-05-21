package dao;

import db.DBConnection;
import model.Department;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDAO {

    public List<Department> getAll() throws SQLException {
        String sql = "SELECT dept_id, dept_name, dept_code, is_active FROM department ORDER BY dept_id";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Department> list = new ArrayList<>();

            while (rs.next()) {
                Department d = new Department(
                        rs.getInt("dept_id"),
                        rs.getString("dept_name"),
                        rs.getString("dept_code"),
                        rs.getInt("is_active")
                );
                list.add(d);
            }
            return list;
        }
    }

    public boolean insert(String name, String code, int isActive) throws SQLException {
        String sql = "INSERT INTO department (dept_name, dept_code, is_active) VALUES (?, ?, ?)";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, code);
            ps.setInt(3, isActive);

            return ps.executeUpdate() == 1;
        }
    }

    public boolean update(int id, String name, String code, int isActive) throws SQLException {
        String sql = "UPDATE department SET dept_name = ?, dept_code = ?, is_active = ? WHERE dept_id = ?";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, code);
            ps.setInt(3, isActive);
            ps.setInt(4, id);

            return ps.executeUpdate() == 1;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM department WHERE dept_id = ?";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        }
    }
}
