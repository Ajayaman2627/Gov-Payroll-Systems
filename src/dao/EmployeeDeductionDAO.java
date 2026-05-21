package dao;

import model.EmployeeDeduction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDeductionDAO {

    public void addEmployeeDeduction(EmployeeDeduction d) throws SQLException {
        String sql = "INSERT INTO employee_deduction(employee_id, deduction_type_id, deduction_mode, deduction_value, active) " +
                     "VALUES(?,?,?,?,?)";

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, d.getEmployeeId());
            ps.setInt(2, d.getDeductionTypeId());
            ps.setString(3, d.getDeductionMode());
            ps.setDouble(4, d.getDeductionValue());
            ps.setBoolean(5, d.isActive());
            ps.executeUpdate();
        }
    }

    public void updateEmployeeDeduction(EmployeeDeduction d) throws SQLException {
        String sql = "UPDATE employee_deduction " +
                     "SET employee_id=?, deduction_type_id=?, deduction_mode=?, deduction_value=?, active=? " +
                     "WHERE employee_deduction_id=?";

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, d.getEmployeeId());
            ps.setInt(2, d.getDeductionTypeId());
            ps.setString(3, d.getDeductionMode());
            ps.setDouble(4, d.getDeductionValue());
            ps.setBoolean(5, d.isActive());
            ps.setInt(6, d.getEmployeeDeductionId());
            ps.executeUpdate();
        }
    }

    public void deleteEmployeeDeduction(int id) throws SQLException {
        String sql = "DELETE FROM employee_deduction WHERE employee_deduction_id=?";

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<Object[]> getAllEmployeeDeductionsDetailed() throws SQLException {
        String sql = "SELECT ed.employee_deduction_id, ed.employee_id, " +
                     "CONCAT(COALESCE(e.first_name,''), ' ', COALESCE(e.last_name,'')) AS employee_name, " +
                     "ed.deduction_type_id, dt.deduction_name, ed.deduction_mode, ed.deduction_value, ed.active " +
                     "FROM employee_deduction ed " +
                     "JOIN employee e ON ed.employee_id = e.employee_id " +
                     "JOIN deduction_type dt ON ed.deduction_type_id = dt.deduction_type_id " +
                     "ORDER BY ed.employee_deduction_id DESC";

        List<Object[]> list = new ArrayList<>();

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Object[]{
                        rs.getInt("employee_deduction_id"),
                        rs.getInt("employee_id"),
                        rs.getString("employee_name").trim(),
                        rs.getInt("deduction_type_id"),
                        rs.getString("deduction_name"),
                        rs.getString("deduction_mode"),
                        rs.getDouble("deduction_value"),
                        rs.getBoolean("active")
                });
            }
        }
        return list;
    }

    public List<EmployeeDeduction> getDeductionsByEmployee(int employeeId) throws SQLException {
        String sql = "SELECT employee_deduction_id, employee_id, deduction_type_id, deduction_mode, deduction_value, active " +
                     "FROM employee_deduction WHERE employee_id=? AND active=TRUE";

        List<EmployeeDeduction> list = new ArrayList<>();

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, employeeId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new EmployeeDeduction(
                            rs.getInt("employee_deduction_id"),
                            rs.getInt("employee_id"),
                            rs.getInt("deduction_type_id"),
                            rs.getString("deduction_mode"),
                            rs.getDouble("deduction_value"),
                            rs.getBoolean("active")
                    ));
                }
            }
        }
        return list;
    }

    public double getBasicSalary(int employeeId) throws SQLException {
        String sql = "SELECT basic_salary FROM employee WHERE employee_id=?";

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, employeeId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("basic_salary");
                }
            }
        }
        return 0.0;
    }

    public double calculateDeductionAmount(EmployeeDeduction d) throws SQLException {
        if ("AMOUNT".equalsIgnoreCase(d.getDeductionMode())) {
            return d.getDeductionValue();
        } else if ("PERCENT".equalsIgnoreCase(d.getDeductionMode())) {
            double basicSalary = getBasicSalary(d.getEmployeeId());
            return basicSalary * d.getDeductionValue() / 100.0;
        }
        return 0.0;
    }

    public double getTotalDeductions(int employeeId) throws SQLException {
        List<EmployeeDeduction> list = getDeductionsByEmployee(employeeId);
        double total = 0.0;

        for (EmployeeDeduction d : list) {
            total += calculateDeductionAmount(d);
        }

        return total;
    }
}