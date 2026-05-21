package ui;

import dao.AllowanceTypeDAO;
import dao.EmployeeAllowanceDAO;
import dao.EmployeeMiniDAO;
import model.AllowanceType;
import model.EmployeeAllowance;
import model.EmployeeMini;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class EmployeeAllowancesFrame extends JFrame {

    private final EmployeeMiniDAO employeeDAO = new EmployeeMiniDAO();
    private final AllowanceTypeDAO typeDAO = new AllowanceTypeDAO();
    private final EmployeeAllowanceDAO allowanceDAO = new EmployeeAllowanceDAO();

    private final JComboBox<EmployeeMini> employeeBox = new JComboBox<>();

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"ID", "Type", "Mode", "Amount", "Percent", "Effective From", "Effective To"}, 0
    ) {
        @Override public boolean isCellEditable(int row, int col) { return false; }
    };

    private final JTable table = new JTable(tableModel);

    private final JLabel basicSalaryLabel = new JLabel("Basic Salary: -");
    private final JLabel totalAllowancesLabel = new JLabel("Total Allowances: -");

    public EmployeeAllowancesFrame() {
        super("Employee Allowances");

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1100, 520);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // top controls
        JPanel top = new JPanel(new BorderLayout(10, 10));
        JLabel title = new JLabel("Employee Allowances");
        title.setFont(new Font("Dialog", Font.BOLD, 18));
        top.add(title, BorderLayout.NORTH);

        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        row.add(new JLabel("Employee:"));
        employeeBox.setPreferredSize(new Dimension(320, 28));
        row.add(employeeBox);

        JButton loadBtn = new JButton("Load");
        loadBtn.addActionListener(e -> loadAllowances());
        row.add(loadBtn);

        row.add(Box.createHorizontalStrut(20));
        row.add(basicSalaryLabel);
        row.add(Box.createHorizontalStrut(20));
        row.add(totalAllowancesLabel);

        top.add(row, BorderLayout.CENTER);

        root.add(top, BorderLayout.NORTH);

        // table
        table.setRowHeight(26);
        root.add(new JScrollPane(table), BorderLayout.CENTER);

        // actions
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(e -> loadAllowances());

        JButton add = new JButton("Add");
        add.addActionListener(e -> onAdd());

        JButton edit = new JButton("Edit");
        edit.addActionListener(e -> onEdit());

        JButton delete = new JButton("Delete");
        delete.addActionListener(e -> onDelete());

        actions.add(refresh);
        actions.add(add);
        actions.add(edit);
        actions.add(delete);

        root.add(actions, BorderLayout.SOUTH);

        setContentPane(root);

        loadEmployees();
    }

    private void loadEmployees() {
        employeeBox.removeAllItems();
        try {
            List<EmployeeMini> list = employeeDAO.getAllEmployees();
            for (EmployeeMini e : list) employeeBox.addItem(e);

            if (employeeBox.getItemCount() > 0) {
                employeeBox.setSelectedIndex(0);
                loadAllowances();
            }
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private int selectedEmployeeId() {
        EmployeeMini e = (EmployeeMini) employeeBox.getSelectedItem();
        return e == null ? -1 : e.getEmployeeId();
    }

    private void loadAllowances() {
        int employeeId = selectedEmployeeId();
        if (employeeId <= 0) return;

        tableModel.setRowCount(0);

        try {
            double basicSalary = employeeDAO.getBasicSalary(employeeId);
            basicSalaryLabel.setText("Basic Salary: " + String.format("%.2f", basicSalary));

            List<EmployeeAllowance> list = allowanceDAO.getByEmployee(employeeId);
            for (EmployeeAllowance ea : list) {
                tableModel.addRow(new Object[]{
                        ea.getEmployeeAllowanceId(),
                        ea.getAllowanceTypeName(),
                        ea.getCalcMode().name(),
                        ea.getAmount(),
                        ea.getPercent(),
                        ea.getEffectiveFrom(),
                        ea.getEffectiveTo()
                });
            }

            BigDecimal total = allowanceDAO.computeTotalAllowances(employeeId, BigDecimal.valueOf(basicSalary));
            totalAllowancesLabel.setText("Total Allowances: " + total);

        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void onAdd() {
        int employeeId = selectedEmployeeId();
        if (employeeId <= 0) return;

        try {
            List<AllowanceType> types = typeDAO.getAll(true);
            if (types.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No active allowance types. Create types first.");
                return;
            }

            EmployeeAllowanceFormDialog dlg = new EmployeeAllowanceFormDialog(this, "Add Employee Allowance", null, types);
            dlg.setVisible(true);
            if (!dlg.isSaved()) return;

            EmployeeAllowance ea = dlg.getValue();
            ea.setEmployeeId(employeeId);

            allowanceDAO.insert(ea);
            loadAllowances();

        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void onEdit() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a row first.");
            return;
        }

        int employeeAllowanceId = (int) tableModel.getValueAt(row, 0);
        String typeName = String.valueOf(tableModel.getValueAt(row, 1));
        String mode = String.valueOf(tableModel.getValueAt(row, 2));

        try {
            List<AllowanceType> types = typeDAO.getAll(true);

            // Build existing object from row (good enough for editing)
            EmployeeAllowance existing = new EmployeeAllowance();
            existing.setEmployeeAllowanceId(employeeAllowanceId);
            existing.setAllowanceTypeName(typeName);
            existing.setAmount((BigDecimal) tableModel.getValueAt(row, 3));
            existing.setPercent((BigDecimal) tableModel.getValueAt(row, 4));
            existing.setCalcMode(model.AllowanceCalcMode.valueOf(mode));

            // We must map typeName → id
            int typeId = -1;
            for (AllowanceType t : types) {
                if (t.getName().equals(typeName)) { typeId = t.getAllowanceTypeId(); break; }
            }
            if (typeId == -1) {
                JOptionPane.showMessageDialog(this, "Type not found among active types. Re-activate it or change type.");
                return;
            }
            existing.setAllowanceTypeId(typeId);

            // effective dates
            Object ef = tableModel.getValueAt(row, 5);
            Object et = tableModel.getValueAt(row, 6);
            if (ef != null) existing.setEffectiveFrom(java.time.LocalDate.parse(ef.toString()));
            if (et != null) existing.setEffectiveTo(java.time.LocalDate.parse(et.toString()));

            EmployeeAllowanceFormDialog dlg = new EmployeeAllowanceFormDialog(this, "Edit Employee Allowance", existing, types);
            dlg.setVisible(true);
            if (!dlg.isSaved()) return;

            EmployeeAllowance updated = dlg.getValue();
            updated.setEmployeeAllowanceId(employeeAllowanceId);

            allowanceDAO.update(updated);
            loadAllowances();

        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void onDelete() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a row first.");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        String typeName = String.valueOf(tableModel.getValueAt(row, 1));

        int ok = JOptionPane.showConfirmDialog(this,
                "Delete allowance: " + typeName + " ?",
                "Confirm", JOptionPane.YES_NO_OPTION);

        if (ok != JOptionPane.YES_OPTION) return;

        try {
            allowanceDAO.delete(id);
            loadAllowances();
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void showError(Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}