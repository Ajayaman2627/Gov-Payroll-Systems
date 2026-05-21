package dao;

import db.DBConnection;
import model.Grade;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class GradeDAO {

    public List<Grade> getAll() throws Exception {
        String sql = "SELECT grade_id, grade_name FROM grade ORDER BY grade_id";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Grade> list = new ArrayList<>();
            while (rs.next()) {
                Grade g = new Grade(rs.getInt("grade_id"), rs.getString("grade_name"));
                list.add(g);
            }
            return list;
        }
    }
}