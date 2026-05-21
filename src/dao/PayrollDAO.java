package dao;

import app.Session;
import model.PayrollRecord;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PayrollDAO {

    public boolean runAlreadyExists(int month, int year) throws SQLException {
        String sql = "SELECT COUNT(*) FROM payroll_run WHERE pay_period_month = ? AND pay_period_year = ?";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, month);
            ps.setInt(2, year);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public int runPayroll(int month, int year) throws SQLException {
        int runId = createRun(month, year);
        if (runId < 0) return -1;

        String empSql = "SELECT emp_id, basic_salary FROM employee WHERE is_active = 1";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(empSql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int empId = rs.getInt("emp_id");
                BigDecimal basic = rs.getBigDecimal("basic_salary");
                BigDecimal allowances = computeAllowances(empId, basic);
                BigDecimal gross = basic.add(allowances);
                BigDecimal deductions = computeDeductions(empId, basic);
                BigDecimal net = gross.subtract(deductions);
                insertRecord(runId, empId, basic, allowances, gross, deductions, net);
            }
        }
        return runId;
    }

    private int createRun(int month, int year) throws SQLException {
        String sql =
            "INSERT INTO payroll_run (run_date, pay_period_month, pay_period_year, run_by_user_id, status) " +
            "VALUES (CURDATE(), ?, ?, ?, 'COMPLETED')";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, month);
            ps.setInt(2, year);
            ps.setInt(3, Session.userId);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }

    private BigDecimal computeAllowances(int empId, BigDecimal basic) throws SQLException {
        String sql =
            "SELECT COALESCE(SUM(CASE " +
            "  WHEN calc_mode='AMOUNT'  THEN amount " +
            "  WHEN calc_mode='PERCENT' THEN (? * percent / 100) " +
            "  ELSE 0 END), 0) FROM employee_allowance WHERE emp_id = ?";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setBigDecimal(1, basic);
            ps.setInt(2, empId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getBigDecimal(1);
            }
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal computeDeductions(int empId, BigDecimal basic) throws SQLException {
        String sql =
            "SELECT COALESCE(SUM(CASE " +
            "  WHEN deduction_mode='AMOUNT'  THEN deduction_value " +
            "  WHEN deduction_mode='PERCENT' THEN (? * deduction_value / 100) " +
            "  ELSE 0 END), 0) FROM employee_deduction WHERE employee_id = ? AND active = TRUE";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setBigDecimal(1, basic);
            ps.setInt(2, empId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getBigDecimal(1);
            }
        }
        return BigDecimal.ZERO;
    }

    private void insertRecord(int runId, int empId, BigDecimal basic, BigDecimal allowances,
                              BigDecimal gross, BigDecimal deductions, BigDecimal net) throws SQLException {
        String sql =
            "INSERT INTO payroll_record " +
            "(run_id, emp_id, basic_salary, total_allowances, gross_pay, total_deductions, net_pay) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, runId);
            ps.setInt(2, empId);
            ps.setBigDecimal(3, basic);
            ps.setBigDecimal(4, allowances);
            ps.setBigDecimal(5, gross);
            ps.setBigDecimal(6, deductions);
            ps.setBigDecimal(7, net);
            ps.executeUpdate();
        }
    }

    public List<PayrollRecord> getRecordsByRun(int runId) throws SQLException {
        String sql =
            "SELECT pr.record_id, pr.emp_id, " +
            "CONCAT(e.first_name, ' ', e.last_name) AS emp_name, " +
            "d.dept_name, COALESCE(g.grade_name, '') AS grade_name, e.step_no, " +
            "pr.basic_salary, pr.total_allowances, pr.gross_pay, pr.total_deductions, pr.net_pay " +
            "FROM payroll_record pr " +
            "JOIN employee e ON e.emp_id = pr.emp_id " +
            "JOIN department d ON d.dept_id = e.dept_id " +
            "LEFT JOIN grade g ON g.grade_id = e.grade_id " +
            "WHERE pr.run_id = ? ORDER BY e.emp_id";

        List<PayrollRecord> list = new ArrayList<>();
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, runId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PayrollRecord rec = new PayrollRecord();
                    rec.setRecordId(rs.getInt("record_id"));
                    rec.setRunId(runId);
                    rec.setEmpId(rs.getInt("emp_id"));
                    rec.setEmpName(rs.getString("emp_name").trim());
                    rec.setDeptName(rs.getString("dept_name"));
                    rec.setGradeName(rs.getString("grade_name"));
                    rec.setStepNo(rs.getInt("step_no"));
                    rec.setBasicSalary(rs.getBigDecimal("basic_salary"));
                    rec.setTotalAllowances(rs.getBigDecimal("total_allowances"));
                    rec.setGrossPay(rs.getBigDecimal("gross_pay"));
                    rec.setTotalDeductions(rs.getBigDecimal("total_deductions"));
                    rec.setNetPay(rs.getBigDecimal("net_pay"));
                    list.add(rec);
                }
            }
        }
        return list;
    }

    // Returns [run_id, month, year, run_date]
    public List<Object[]> getAllRuns() throws SQLException {
        String sql =
            "SELECT run_id, pay_period_month, pay_period_year, run_date " +
            "FROM payroll_run ORDER BY run_id DESC";
        List<Object[]> list = new ArrayList<>();
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Object[]{
                    rs.getInt("run_id"),
                    rs.getInt("pay_period_month"),
                    rs.getInt("pay_period_year"),
                    rs.getString("run_date")
                });
            }
        }
        return list;
    }

    // For payslip: [type_name, calc_mode, raw_value, computed_amount]
    public List<Object[]> getAllowanceDetail(int empId) throws SQLException {
        String sql =
            "SELECT at.name, ea.calc_mode, " +
            "  CASE WHEN ea.calc_mode='AMOUNT' THEN ea.amount ELSE ea.percent END AS raw_value, " +
            "  CASE WHEN ea.calc_mode='AMOUNT'  THEN ea.amount " +
            "       WHEN ea.calc_mode='PERCENT' THEN (e.basic_salary * ea.percent / 100) " +
            "       ELSE 0 END AS computed " +
            "FROM employee_allowance ea " +
            "JOIN allowance_type at ON at.allow_type_id = ea.allow_type_id " +
            "JOIN employee e ON e.emp_id = ea.emp_id " +
            "WHERE ea.emp_id = ? ORDER BY at.name";

        List<Object[]> list = new ArrayList<>();
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, empId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{
                        rs.getString("name"),
                        rs.getString("calc_mode"),
                        rs.getBigDecimal("raw_value"),
                        rs.getBigDecimal("computed")
                    });
                }
            }
        }
        return list;
    }

    // For payslip: [type_name, mode, raw_value, computed_amount]
    public List<Object[]> getDeductionDetail(int empId) throws SQLException {
        String sql =
            "SELECT dt.deduction_name, ed.deduction_mode, ed.deduction_value, " +
            "  CASE WHEN ed.deduction_mode='AMOUNT'  THEN ed.deduction_value " +
            "       WHEN ed.deduction_mode='PERCENT' THEN (e.basic_salary * ed.deduction_value / 100) " +
            "       ELSE 0 END AS computed " +
            "FROM employee_deduction ed " +
            "JOIN deduction_type dt ON dt.deduction_type_id = ed.deduction_type_id " +
            "JOIN employee e ON e.emp_id = ed.employee_id " +
            "WHERE ed.employee_id = ? AND ed.active = TRUE ORDER BY dt.deduction_name";

        List<Object[]> list = new ArrayList<>();
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, empId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{
                        rs.getString("deduction_name"),
                        rs.getString("deduction_mode"),
                        rs.getDouble("deduction_value"),
                        rs.getDouble("computed")
                    });
                }
            }
        }
        return list;
    }
}
