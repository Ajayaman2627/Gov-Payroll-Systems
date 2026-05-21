package dao;

import model.AllowanceCalcMode;
import model.EmployeeAllowance;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeAllowanceDAO {

    public List<EmployeeAllowance> getByEmployee(int employeeId) throws SQLException {
        String sql =
                "SELECT ea.employee_allowance_id, " +
                "       ea.emp_id AS employee_id, " +
                "       ea.allow_type_id AS allowance_type_id, " +
                "       at.name AS type_name, " +
                "       ea.calc_mode, ea.amount, ea.percent, ea.effective_from, ea.effective_to " +
                "FROM employee_allowance ea " +
                "JOIN allowance_type at ON at.allow_type_id = ea.allow_type_id " +
                "WHERE ea.emp_id=? " +
                "ORDER BY at.name";

        List<EmployeeAllowance> list = new ArrayList<>();

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, employeeId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    EmployeeAllowance ea = new EmployeeAllowance();
                    ea.setEmployeeAllowanceId(rs.getInt("employee_allowance_id"));
                    ea.setEmployeeId(rs.getInt("employee_id"));
                    ea.setAllowanceTypeId(rs.getInt("allowance_type_id"));
                    ea.setAllowanceTypeName(rs.getString("type_name"));
                    ea.setCalcMode(AllowanceCalcMode.valueOf(rs.getString("calc_mode")));
                    ea.setAmount(rs.getBigDecimal("amount"));
                    ea.setPercent(rs.getBigDecimal("percent"));

                    Date ef = rs.getDate("effective_from");
                    Date et = rs.getDate("effective_to");
                    ea.setEffectiveFrom(ef == null ? null : ef.toLocalDate());
                    ea.setEffectiveTo(et == null ? null : et.toLocalDate());

                    list.add(ea);
                }
            }
        }

        return list;
    }

    public int insert(EmployeeAllowance ea) throws SQLException {
        String sql =
                "INSERT INTO employee_allowance " +
                "(emp_id, allow_type_id, calc_mode, amount, percent, effective_from, effective_to) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, ea.getEmployeeId());
            ps.setInt(2, ea.getAllowanceTypeId());
            ps.setString(3, ea.getCalcMode().name());

            if (ea.getCalcMode() == AllowanceCalcMode.AMOUNT) {
                ps.setBigDecimal(4, ea.getAmount());
                ps.setNull(5, Types.DECIMAL);
            } else {
                ps.setNull(4, Types.DECIMAL);
                ps.setBigDecimal(5, ea.getPercent());
            }

            if (ea.getEffectiveFrom() == null) ps.setNull(6, Types.DATE);
            else ps.setDate(6, Date.valueOf(ea.getEffectiveFrom()));

            if (ea.getEffectiveTo() == null) ps.setNull(7, Types.DATE);
            else ps.setDate(7, Date.valueOf(ea.getEffectiveTo()));

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }

        return -1;
    }

    public void update(EmployeeAllowance ea) throws SQLException {
        String sql =
                "UPDATE employee_allowance " +
                "SET allow_type_id=?, calc_mode=?, amount=?, percent=?, effective_from=?, effective_to=? " +
                "WHERE employee_allowance_id=?";

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, ea.getAllowanceTypeId());
            ps.setString(2, ea.getCalcMode().name());

            if (ea.getCalcMode() == AllowanceCalcMode.AMOUNT) {
                ps.setBigDecimal(3, ea.getAmount());
                ps.setNull(4, Types.DECIMAL);
            } else {
                ps.setNull(3, Types.DECIMAL);
                ps.setBigDecimal(4, ea.getPercent());
            }

            if (ea.getEffectiveFrom() == null) ps.setNull(5, Types.DATE);
            else ps.setDate(5, Date.valueOf(ea.getEffectiveFrom()));

            if (ea.getEffectiveTo() == null) ps.setNull(6, Types.DATE);
            else ps.setDate(6, Date.valueOf(ea.getEffectiveTo()));

            ps.setInt(7, ea.getEmployeeAllowanceId());
            ps.executeUpdate();
        }
    }

    public void delete(int employeeAllowanceId) throws SQLException {
        String sql = "DELETE FROM employee_allowance WHERE employee_allowance_id=?";

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, employeeAllowanceId);
            ps.executeUpdate();
        }
    }

    public BigDecimal computeTotalAllowances(int employeeId, BigDecimal basicSalary) throws SQLException {
        String sql =
                "SELECT COALESCE(SUM(CASE " +
                "  WHEN calc_mode='AMOUNT' THEN amount " +
                "  WHEN calc_mode='PERCENT' THEN (? * (percent / 100)) " +
                "  ELSE 0 END), 0) AS total " +
                "FROM employee_allowance " +
                "WHERE emp_id=?";

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setBigDecimal(1, basicSalary);
            ps.setInt(2, employeeId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getBigDecimal("total");
            }
        }

        return BigDecimal.ZERO;
    }
}