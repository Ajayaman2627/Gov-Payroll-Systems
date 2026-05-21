package dao;

import model.AllowanceType;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AllowanceTypeDAO {

    public List<AllowanceType> getAll(boolean includeInactive) throws SQLException {
        List<AllowanceType> list = new ArrayList<>();

        String sql =
                "SELECT allow_type_id, name, calc_type, default_value, is_taxable " +
                "FROM allowance_type " +
                "ORDER BY name";

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                AllowanceType a = new AllowanceType();
                a.setAllowanceTypeId(rs.getInt("allow_type_id"));
                a.setName(rs.getString("name"));
                a.setCalcType(rs.getString("calc_type"));
                a.setDefaultValue(rs.getBigDecimal("default_value"));
                a.setTaxable(rs.getBoolean("is_taxable"));
                list.add(a);
            }
        }

        return list;
    }

    public int insert(AllowanceType a) throws SQLException {
        String sql =
                "INSERT INTO allowance_type (name, calc_type, default_value, is_taxable) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, a.getName());
            ps.setString(2, a.getCalcType());
            ps.setBigDecimal(3, a.getDefaultValue());
            ps.setBoolean(4, a.isTaxable());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        return -1;
    }

    public void update(AllowanceType a) throws SQLException {
        String sql =
                "UPDATE allowance_type " +
                "SET name = ?, calc_type = ?, default_value = ?, is_taxable = ? " +
                "WHERE allow_type_id = ?";

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, a.getName());
            ps.setString(2, a.getCalcType());
            ps.setBigDecimal(3, a.getDefaultValue());
            ps.setBoolean(4, a.isTaxable());
            ps.setInt(5, a.getAllowanceTypeId());

            ps.executeUpdate();
        }
    }

    public void softDelete(int id) throws SQLException {
        // This table has no active column, so use hard delete instead.
        String sql = "DELETE FROM allowance_type WHERE allow_type_id = ?";

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}