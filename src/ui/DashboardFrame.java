package ui;

import app.Session;

import javax.swing.*;
import java.awt.*;

public class DashboardFrame extends JFrame {

    public DashboardFrame() {
        super("Government Payroll System - Dashboard");

        // ----- Welcome Label -----
        String username = (Session.username != null) ? Session.username : "user";
        String role = (Session.roleName != null) ? Session.roleName : "UNKNOWN";

        JLabel welcome = new JLabel("Welcome, " + username + "  |  Role: " + role);
        welcome.setFont(new Font("Dialog", Font.BOLD, 20));
        welcome.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ----- Buttons -----
        JButton btnDepartments = new JButton("Departments");
        JButton btnEmployees   = new JButton("Employees");
        JButton btnPayStruct   = new JButton("Pay Structure");
        JButton btnPayrollRun  = new JButton("Payroll Run");
        JButton btnAllowances  = new JButton("Allowances");
        JButton btnDeductions  = new JButton("Deductions");
        JButton btnLogout      = new JButton("Logout");

        // Make buttons bigger and cleaner
        Dimension tileSize = new Dimension(220, 120);
        for (JButton b : new JButton[]{
                btnDepartments, btnEmployees, btnPayStruct,
                btnPayrollRun, btnAllowances, btnDeductions, btnLogout}) {
            b.setPreferredSize(tileSize);
            b.setFocusPainted(false);
            b.setFont(new Font("Dialog", Font.PLAIN, 16));
        }

        // ----- Grid Layout -----
        JPanel grid = new JPanel(new GridLayout(2, 4, 12, 12));
        grid.add(btnDepartments);
        grid.add(btnEmployees);
        grid.add(btnPayStruct);
        grid.add(btnPayrollRun);
        grid.add(btnAllowances);
        grid.add(btnDeductions);
        grid.add(btnLogout);

        // ----- Button Actions -----

        // Departments → real frame
        btnDepartments.addActionListener(e -> new DepartmentsFrame().setVisible(true));

        // Employees → real frame (THIS is the correct one)
        btnEmployees.addActionListener(e -> new EmployeesFrame().setVisible(true));

        btnPayStruct.addActionListener(e -> new PayStructureFrame().setVisible(true));
        btnPayrollRun.addActionListener(e -> new PayrollRunFrame().setVisible(true));

        // ✅ Allowances → real menu frame
        btnAllowances.addActionListener(e -> new AllowancesMenuFrame().setVisible(true));

        btnDeductions.addActionListener(e -> new DeductionsFrame().setVisible(true));

        // Logout
        btnLogout.addActionListener(e -> {
            Session.userId = 0;
            Session.username = null;
            Session.roleName = null;
            new LoginFrame().setVisible(true);
            dispose();
        });

        // ----- Main Layout -----
        JPanel root = new JPanel(new BorderLayout(15, 15));
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        root.add(welcome, BorderLayout.NORTH);
        root.add(grid, BorderLayout.CENTER);

        add(root);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
    }

    // Placeholder for not-yet-built screens
    private void openPlaceholder(String title) {
        JFrame frame = new JFrame(title + " - Coming Soon");
        frame.setSize(500, 300);
        frame.setLocationRelativeTo(this);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel label = new JLabel(title + " module coming soon...", SwingConstants.CENTER);
        label.setFont(new Font("Dialog", Font.PLAIN, 18));
        frame.add(label);

        frame.setVisible(true);
    }
}