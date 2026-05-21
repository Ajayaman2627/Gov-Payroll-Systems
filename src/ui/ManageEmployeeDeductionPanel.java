package ui;

import dao.DeductionTypeDAO;
import dao.EmployeeDeductionDAO;
import dao.EmployeeMiniDAO;
import model.DeductionType;
import model.EmployeeDeduction;
import model.EmployeeMini;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class ManageEmployeeDeductionPanel extends JPanel {

    private JComboBox<EmployeeMini> cmbEmployee;
    private JComboBox<DeductionType> cmbDeductionType;
    private JComboBox<String> cmbMode;
    private JTextField txtValue;
    private JCheckBox chkActive;

    private JTable table;
    private DefaultTableModel model;

    private final EmployeeMiniDAO employeeMiniDAO = new EmployeeMiniDAO();
    private final DeductionTypeDAO deductionTypeDAO = new DeductionTypeDAO();
    private final EmployeeDeductionDAO employeeDeductionDAO = new EmployeeDeductionDAO();

    private int selectedId = -1;

    public ManageEmployeeDeductionPanel() {
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Employee Deduction Details"));

        formPanel.add(new JLabel("Employee:"));
        cmbEmployee = new JComboBox<>();
        formPanel.add(cmbEmployee);

        formPanel.add(new JLabel("Deduction Type:"));
        cmbDeductionType = new JComboBox<>();
        formPanel.add(cmbDeductionType);

        formPanel.add(new JLabel("Mode:"));
        cmbMode = new JComboBox<>(new String[]{"AMOUNT", "PERCENT"});
        formPanel.add(cmbMode);

        formPanel.add(new JLabel("Value:"));
        txtValue = new JTextField();
        formPanel.add(txtValue);

        formPanel.add(new JLabel("Active:"));
        chkActive = new JCheckBox();
        chkActive.setSelected(true);
        formPanel.add(chkActive);

        JPanel buttonPanel = new JPanel();
        JButton btnAdd = new JButton("Add");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");
        JButton btnClear = new JButton("Clear");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);

        model = new DefaultTableModel(
                new String[]{"ID", "Emp ID", "Employee", "Type ID", "Deduction Type", "Mode", "Value", "Active"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        add(formPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        loadEmployees();
        loadDeductionTypes();
        loadEmployeeDeductions();

        btnAdd.addActionListener(e -> addEmployeeDeduction());
        btnUpdate.addActionListener(e -> updateEmployeeDeduction());
        btnDelete.addActionListener(e -> deleteEmployeeDeduction());
        btnClear.addActionListener(e -> clearForm());

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                fillFormFromTable();
            }
        });
    }

    private void loadEmployees() {
        cmbEmployee.removeAllItems();
        try {
            List<EmployeeMini> employees = employeeMiniDAO.getAllEmployees();
            for (EmployeeMini emp : employees) {
                cmbEmployee.addItem(emp);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading employees: " + ex.getMessage());
        }
    }

    private void loadDeductionTypes() {
        cmbDeductionType.removeAllItems();
        try {
            List<DeductionType> types = deductionTypeDAO.getAllDeductionTypes();
            for (DeductionType type : types) {
                cmbDeductionType.addItem(type);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading deduction types: " + ex.getMessage());
        }
    }

    private void loadEmployeeDeductions() {
        model.setRowCount(0);
        try {
            List<Object[]> rows = employeeDeductionDAO.getAllEmployeeDeductionsDetailed();
            for (Object[] row : rows) {
                model.addRow(row);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading employee deductions: " + ex.getMessage());
        }
    }

    private void addEmployeeDeduction() {
        EmployeeMini employee = (EmployeeMini) cmbEmployee.getSelectedItem();
        DeductionType type = (DeductionType) cmbDeductionType.getSelectedItem();
        String mode = (String) cmbMode.getSelectedItem();
        String valueText = txtValue.getText().trim();

        if (employee == null || type == null || mode == null || valueText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return;
        }

        double value;
        try {
            value = Double.parseDouble(valueText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Value must be numeric.");
            return;
        }

        if (value < 0) {
            JOptionPane.showMessageDialog(this, "Value cannot be negative.");
            return;
        }

        if ("PERCENT".equals(mode) && value > 100) {
            JOptionPane.showMessageDialog(this, "Percent cannot be greater than 100.");
            return;
        }

        EmployeeDeduction d = new EmployeeDeduction();
        d.setEmployeeId(employee.getEmployeeId());
        d.setDeductionTypeId(type.getDeductionTypeId());
        d.setDeductionMode(mode);
        d.setDeductionValue(value);
        d.setActive(chkActive.isSelected());

        try {
            employeeDeductionDAO.addEmployeeDeduction(d);
            JOptionPane.showMessageDialog(this, "Employee deduction added successfully.");
            clearForm();
            loadEmployeeDeductions();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error adding employee deduction: " + ex.getMessage());
        }
    }

    private void updateEmployeeDeduction() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Select a row to update.");
            return;
        }

        EmployeeMini employee = (EmployeeMini) cmbEmployee.getSelectedItem();
        DeductionType type = (DeductionType) cmbDeductionType.getSelectedItem();
        String mode = (String) cmbMode.getSelectedItem();
        String valueText = txtValue.getText().trim();

        if (employee == null || type == null || mode == null || valueText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return;
        }

        double value;
        try {
            value = Double.parseDouble(valueText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Value must be numeric.");
            return;
        }

        if (value < 0) {
            JOptionPane.showMessageDialog(this, "Value cannot be negative.");
            return;
        }

        if ("PERCENT".equals(mode) && value > 100) {
            JOptionPane.showMessageDialog(this, "Percent cannot be greater than 100.");
            return;
        }

        EmployeeDeduction d = new EmployeeDeduction();
        d.setEmployeeDeductionId(selectedId);
        d.setEmployeeId(employee.getEmployeeId());
        d.setDeductionTypeId(type.getDeductionTypeId());
        d.setDeductionMode(mode);
        d.setDeductionValue(value);
        d.setActive(chkActive.isSelected());

        try {
            employeeDeductionDAO.updateEmployeeDeduction(d);
            JOptionPane.showMessageDialog(this, "Employee deduction updated successfully.");
            clearForm();
            loadEmployeeDeductions();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating employee deduction: " + ex.getMessage());
        }
    }

    private void deleteEmployeeDeduction() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Select a row to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this employee deduction?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            employeeDeductionDAO.deleteEmployeeDeduction(selectedId);
            JOptionPane.showMessageDialog(this, "Employee deduction deleted successfully.");
            clearForm();
            loadEmployeeDeductions();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error deleting employee deduction: " + ex.getMessage());
        }
    }

    private void clearForm() {
        selectedId = -1;
        txtValue.setText("");
        chkActive.setSelected(true);
        table.clearSelection();

        if (cmbEmployee.getItemCount() > 0) {
            cmbEmployee.setSelectedIndex(0);
        }
        if (cmbDeductionType.getItemCount() > 0) {
            cmbDeductionType.setSelectedIndex(0);
        }
        cmbMode.setSelectedIndex(0);
    }

    private void fillFormFromTable() {
        int row = table.getSelectedRow();
        if (row == -1) {
            return;
        }

        selectedId = Integer.parseInt(model.getValueAt(row, 0).toString());
        int employeeId = Integer.parseInt(model.getValueAt(row, 1).toString());
        int typeId = Integer.parseInt(model.getValueAt(row, 3).toString());
        String mode = model.getValueAt(row, 5).toString();
        String value = model.getValueAt(row, 6).toString();
        boolean active = Boolean.parseBoolean(model.getValueAt(row, 7).toString());

        selectEmployeeById(employeeId);
        selectDeductionTypeById(typeId);
        cmbMode.setSelectedItem(mode);
        txtValue.setText(value);
        chkActive.setSelected(active);
    }

    private void selectEmployeeById(int employeeId) {
        ComboBoxModel<EmployeeMini> comboModel = cmbEmployee.getModel();
        for (int i = 0; i < comboModel.getSize(); i++) {
            EmployeeMini emp = comboModel.getElementAt(i);
            if (emp.getEmployeeId() == employeeId) {
                cmbEmployee.setSelectedIndex(i);
                break;
            }
        }
    }

    private void selectDeductionTypeById(int typeId) {
        ComboBoxModel<DeductionType> comboModel = cmbDeductionType.getModel();
        for (int i = 0; i < comboModel.getSize(); i++) {
            DeductionType type = comboModel.getElementAt(i);
            if (type.getDeductionTypeId() == typeId) {
                cmbDeductionType.setSelectedIndex(i);
                break;
            }
        }
    }
}