package dao;

import model.EmployeeMini;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeMiniDAO {

    public List<EmployeeMini> getAllEmployees() throws SQLException {
        String sql =
                "SELECT emp_id AS employee_id, first_name, last_name " +
                "FROM employee " +
                "ORDER BY emp_id";

        List<EmployeeMini> list = new ArrayList<>();
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("employee_id");
                String name = (rs.getString("first_name") == null ? "" : rs.getString("first_name")) +
                              " " +
                              (rs.getString("last_name") == null ? "" : rs.getString("last_name"));
                list.add(new EmployeeMini(id, name.trim().isEmpty() ? "Employee" : name.trim()));
            }
        }
        return list;
    }

    public double getBasicSalary(int employeeId) throws SQLException {
        String sql = "SELECT basic_salary FROM employee WHERE emp_id=?";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble("basic_salary");
            }
        }
        return 0.0;
    }
}