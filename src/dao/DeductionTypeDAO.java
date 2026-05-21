package dao;

import model.DeductionType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DeductionTypeDAO {

    public void addDeductionType(DeductionType d) throws SQLException {
        String sql = "INSERT INTO deduction_type(deduction_name) VALUES(?)";

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, d.getDeductionName());
            ps.executeUpdate();
        }
    }

    public void updateDeductionType(DeductionType d) throws SQLException {
        String sql = "UPDATE deduction_type SET deduction_name=? WHERE deduction_type_id=?";

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, d.getDeductionName());
            ps.setInt(2, d.getDeductionTypeId());
            ps.executeUpdate();
        }
    }

    public void deleteDeductionType(int id) throws SQLException {
        String sql = "DELETE FROM deduction_type WHERE deduction_type_id=?";

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<DeductionType> getAllDeductionTypes() throws SQLException {
        String sql = "SELECT deduction_type_id, deduction_name FROM deduction_type ORDER BY deduction_name";
        List<DeductionType> list = new ArrayList<>();

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new DeductionType(
                        rs.getInt("deduction_type_id"),
                        rs.getString("deduction_name")
                ));
            }
        }
        return list;
    }

    public DeductionType getDeductionTypeById(int id) throws SQLException {
        String sql = "SELECT deduction_type_id, deduction_name FROM deduction_type WHERE deduction_type_id=?";

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new DeductionType(
                            rs.getInt("deduction_type_id"),
                            rs.getString("deduction_name")
                    );
                }
            }
        }
        return null;
    }
}