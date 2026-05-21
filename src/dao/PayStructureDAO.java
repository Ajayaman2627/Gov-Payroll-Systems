package dao;

import model.PayStructure;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PayStructureDAO {

    public List<PayStructure> getAll() throws SQLException {
        String sql =
            "SELECT ps.pay_structure_id, ps.grade_id, g.grade_name, ps.step_no, ps.salary_amount " +
            "FROM pay_structure ps " +
            "JOIN grade g ON g.grade_id = ps.grade_id " +
            "ORDER BY g.grade_name, ps.step_no";

        List<PayStructure> list = new ArrayList<>();
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new PayStructure(
                    rs.getInt("pay_structure_id"),
                    rs.getInt("grade_id"),
                    rs.getString("grade_name"),
                    rs.getInt("step_no"),
                    rs.getBigDecimal("salary_amount")
                ));
            }
        }
        return list;
    }

    public boolean insert(int gradeId, int stepNo, BigDecimal salary) throws SQLException {
        String sql = "INSERT INTO pay_structure (grade_id, step_no, salary_amount) VALUES (?, ?, ?)";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, gradeId);
            ps.setInt(2, stepNo);
            ps.setBigDecimal(3, salary);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean update(int id, BigDecimal salary) throws SQLException {
        String sql = "UPDATE pay_structure SET salary_amount = ? WHERE pay_structure_id = ?";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setBigDecimal(1, salary);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM pay_structure WHERE pay_structure_id = ?";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}
