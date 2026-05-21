package dao;

import db.DBConnection;
import model.Employee;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {

    public List<Employee> getAll() throws SQLException {
        String sql = """
                SELECT e.emp_id, e.first_name, e.last_name, e.email, e.phone, e.dob, e.hire_date,
                       e.basic_salary, e.dept_id, e.grade_id, e.step_no, e.is_active,
                       d.dept_name,
                       g.grade_name
                FROM employee e
                JOIN department d ON d.dept_id = e.dept_id
                LEFT JOIN grade g ON g.grade_id = e.grade_id
                ORDER BY e.emp_id
                """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Employee> list = new ArrayList<>();
            while (rs.next()) {
                Employee e = new Employee(
                        rs.getInt("emp_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getDate("dob"),
                        rs.getDate("hire_date"),
                        rs.getBigDecimal("basic_salary"),
                        rs.getInt("dept_id"),
                        rs.getInt("grade_id"),
                        rs.getInt("step_no"),
                        rs.getInt("is_active"),
                        rs.getString("dept_name"),
                        rs.getString("grade_name")
                );
                list.add(e);
            }
            return list;
        }
    }

    // kept for backward compatibility (no grade/step)
    public boolean insert(String firstName, String lastName, String email, String phone,
                          Date dob, Date hireDate, BigDecimal basicSalary, int deptId, int isActive) throws SQLException {
        String sql = """
                INSERT INTO employee (first_name, last_name, email, phone, dob, hire_date, basic_salary, dept_id, is_active)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setString(3, email);
            ps.setString(4, phone);
            ps.setDate(5, dob);
            ps.setDate(6, hireDate);
            ps.setBigDecimal(7, basicSalary);
            ps.setInt(8, deptId);
            ps.setInt(9, isActive);

            return ps.executeUpdate() > 0;
        }
    }

    // NEW insert includes grade_id + step_no
    public boolean insertWithGrade(String firstName, String lastName, String email, String phone,
                                   Date dob, Date hireDate, BigDecimal basicSalary,
                                   int deptId, int gradeId, int stepNo, int isActive) throws SQLException {

        String sql = """
                INSERT INTO employee (first_name, last_name, email, phone, dob, hire_date, basic_salary,
                                      dept_id, grade_id, step_no, is_active)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setString(3, email);
            ps.setString(4, phone);
            ps.setDate(5, dob);
            ps.setDate(6, hireDate);
            ps.setBigDecimal(7, basicSalary);
            ps.setInt(8, deptId);
            ps.setInt(9, gradeId);
            ps.setInt(10, stepNo);
            ps.setInt(11, isActive);

            return ps.executeUpdate() > 0;
        }
    }

    // kept for backward compatibility (no grade/step)
    public boolean update(int empId, String firstName, String lastName, String email, String phone,
                          Date dob, Date hireDate, BigDecimal basicSalary, int deptId, int isActive) throws SQLException {
        String sql = """
                UPDATE employee
                SET first_name = ?, last_name = ?, email = ?, phone = ?, dob = ?, hire_date = ?, basic_salary = ?, dept_id = ?, is_active = ?
                WHERE emp_id = ?
                """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setString(3, email);
            ps.setString(4, phone);
            ps.setDate(5, dob);
            ps.setDate(6, hireDate);
            ps.setBigDecimal(7, basicSalary);
            ps.setInt(8, deptId);
            ps.setInt(9, isActive);
            ps.setInt(10, empId);

            return ps.executeUpdate() > 0;
        }
    }

    // NEW update includes grade_id + step_no
    public boolean updateWithGrade(int empId, String firstName, String lastName, String email, String phone,
                                   Date dob, Date hireDate, BigDecimal basicSalary,
                                   int deptId, int gradeId, int stepNo, int isActive) throws SQLException {

        String sql = """
                UPDATE employee
                SET first_name = ?, last_name = ?, email = ?, phone = ?, dob = ?, hire_date = ?, basic_salary = ?,
                    dept_id = ?, grade_id = ?, step_no = ?, is_active = ?
                WHERE emp_id = ?
                """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setString(3, email);
            ps.setString(4, phone);
            ps.setDate(5, dob);
            ps.setDate(6, hireDate);
            ps.setBigDecimal(7, basicSalary);
            ps.setInt(8, deptId);
            ps.setInt(9, gradeId);
            ps.setInt(10, stepNo);
            ps.setInt(11, isActive);
            ps.setInt(12, empId);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int empId) throws SQLException {
        String sql = "DELETE FROM employee WHERE emp_id = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, empId);
            return ps.executeUpdate() > 0;
        }
    }
}