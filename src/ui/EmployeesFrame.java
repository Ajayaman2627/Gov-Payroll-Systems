package ui;

import dao.EmployeeDAO;
import dao.DepartmentDAO;
import dao.GradeDAO;
import model.Department;
import model.Employee;
import model.Grade;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

public class EmployeesFrame extends JFrame {

    private final EmployeeDAO employeeDAO = new EmployeeDAO();
    private final DepartmentDAO departmentDAO = new DepartmentDAO();
    private final GradeDAO gradeDAO = new GradeDAO();

    private final JTextField txtFirstName = new JTextField(15);
    private final JTextField txtLastName  = new JTextField(15);
    private final JTextField txtEmail     = new JTextField(20);
    private final JTextField txtPhone     = new JTextField(15);

    private final JTextField txtDob       = new JTextField(10);
    private final JTextField txtHireDate  = new JTextField(10);
    private final JTextField txtSalary    = new JTextField(10);
    private final JTextField txtStepNo    = new JTextField(10);   // NEW

    private final JComboBox<DeptItem> cmbDept = new JComboBox<>();
    private final JComboBox<GradeItem> cmbGrade = new JComboBox<>();
    private final JComboBox<String> cmbActive = new JComboBox<>(new String[]{"1", "0"});

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"ID","First","Last","Email","Phone","DOB","Hire Date","Salary","Dept","Grade","Step","Active"}, 0
    ) {
        @Override public boolean isCellEditable(int row, int col) { return false; }
    };

    private final JTable table = new JTable(tableModel);

    public EmployeesFrame() {
        setTitle("Employees");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6,6,6,6);
        g.fill = GridBagConstraints.HORIZONTAL;

        int r = 0;

        // Row 0
        g.gridx=0; g.gridy=r; form.add(new JLabel("First Name:"), g);
        g.gridx=1; form.add(txtFirstName, g);
        g.gridx=2; form.add(new JLabel("Last Name:"), g);
        g.gridx=3; form.add(txtLastName, g);
        r++;

        // Row 1
        g.gridx=0; g.gridy=r; form.add(new JLabel("Email:"), g);
        g.gridx=1; form.add(txtEmail, g);
        g.gridx=2; form.add(new JLabel("Phone:"), g);
        g.gridx=3; form.add(txtPhone, g);
        r++;

        // Row 2
        g.gridx=0; g.gridy=r; form.add(new JLabel("DOB (yyyy-mm-dd):"), g);
        g.gridx=1; form.add(txtDob, g);
        g.gridx=2; form.add(new JLabel("Hire Date (yyyy-mm-dd):"), g);
        g.gridx=3; form.add(txtHireDate, g);
        r++;

        // Row 3
        g.gridx=0; g.gridy=r; form.add(new JLabel("Basic Salary:"), g);
        g.gridx=1; form.add(txtSalary, g);
        g.gridx=2; form.add(new JLabel("Department:"), g);
        g.gridx=3; form.add(cmbDept, g);
        r++;

        // Row 4
        g.gridx=0; g.gridy=r; form.add(new JLabel("Grade:"), g);
        g.gridx=1; form.add(cmbGrade, g);
        g.gridx=2; form.add(new JLabel("Step No:"), g);
        g.gridx=3; form.add(txtStepNo, g);
        r++;

        // Row 5
        g.gridx=0; g.gridy=r; form.add(new JLabel("Active (1/0):"), g);
        g.gridx=1; form.add(cmbActive, g);
        r++;

        JButton btnAdd = new JButton("Add");
        JButton btnUpdate = new JButton("Update Selected");
        JButton btnDelete = new JButton("Delete Selected");
        JButton btnRefresh = new JButton("Refresh");
        JButton btnClear = new JButton("Clear");

        JPanel buttons = new JPanel();
        buttons.add(btnAdd);
        buttons.add(btnUpdate);
        buttons.add(btnDelete);
        buttons.add(btnRefresh);
        buttons.add(btnClear);

        btnAdd.addActionListener(e -> addEmployee());
        btnUpdate.addActionListener(e -> updateSelected());
        btnDelete.addActionListener(e -> deleteSelected());
        btnRefresh.addActionListener(e -> loadTable());
        btnClear.addActionListener(e -> clearForm());

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                int row = table.getSelectedRow();
                txtFirstName.setText(valueOrEmpty(tableModel.getValueAt(row, 1)));
                txtLastName.setText(valueOrEmpty(tableModel.getValueAt(row, 2)));
                txtEmail.setText(valueOrEmpty(tableModel.getValueAt(row, 3)));
                txtPhone.setText(valueOrEmpty(tableModel.getValueAt(row, 4)));
                txtDob.setText(valueOrEmpty(tableModel.getValueAt(row, 5)));
                txtHireDate.setText(valueOrEmpty(tableModel.getValueAt(row, 6)));
                txtSalary.setText(valueOrEmpty(tableModel.getValueAt(row, 7)));

                selectDeptByName(valueOrEmpty(tableModel.getValueAt(row, 8)));
                selectGradeByName(valueOrEmpty(tableModel.getValueAt(row, 9)));

                txtStepNo.setText(valueOrEmpty(tableModel.getValueAt(row, 10)));
                cmbActive.setSelectedItem(valueOrEmpty(tableModel.getValueAt(row, 11)));
            }
        });

        JScrollPane scroll = new JScrollPane(table);

        JPanel top = new JPanel(new BorderLayout());
        top.add(form, BorderLayout.CENTER);
        top.add(buttons, BorderLayout.SOUTH);

        add(top, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        loadDepartments();
        loadGrades();
        loadTable();
        clearForm();

        setSize(1120, 620);
        setLocationRelativeTo(null);
    }

    private static String valueOrEmpty(Object o){
        return o == null ? "" : o.toString();
    }

    private Date parseDateOrThrow(String input) {
        if (input == null) return null;
        String t = input.trim();
        if (t.isEmpty()) return null;

        if (t.startsWith("(") && t.endsWith(")")) {
            t = t.substring(1, t.length()-1).trim();
        }

        try { return Date.valueOf(t); } catch (Exception ignored) {}

        if (t.matches("^\\d{1,2}[-/]\\d{1,2}[-/]\\d{4}$")) {
            String sep = t.contains("/") ? "/" : "-";
            String[] parts = t.split(sep);
            String mm = parts[0].length() == 1 ? "0" + parts[0] : parts[0];
            String dd = parts[1].length() == 1 ? "0" + parts[1] : parts[1];
            String yyyy = parts[2];
            String iso = yyyy + "-" + mm + "-" + dd;
            return Date.valueOf(iso);
        }

        throw new IllegalArgumentException("Date format must be yyyy-mm-dd (or MM-dd-yyyy / MM/dd/yyyy).");
    }

    private void loadDepartments() {
        cmbDept.removeAllItems();
        try {
            List<Department> deps = departmentDAO.getAll();
            for (Department d : deps) {
                cmbDept.addItem(new DeptItem(d.getId(), d.getName()));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading departments: " + ex.getMessage());
        }
    }

    private void loadGrades() {
        cmbGrade.removeAllItems();
        try {
            List<Grade> grades = gradeDAO.getAll();
            for (Grade gr : grades) {
                cmbGrade.addItem(new GradeItem(gr.getId(), gr.getName()));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading grades: " + ex.getMessage());
        }
    }

    private void selectDeptByName(String deptName){
        for (int i = 0; i < cmbDept.getItemCount(); i++){
            DeptItem it = cmbDept.getItemAt(i);
            if (it != null && it.name.equals(deptName)){
                cmbDept.setSelectedIndex(i);
                return;
            }
        }
    }

    private void selectGradeByName(String gradeName){
        for (int i = 0; i < cmbGrade.getItemCount(); i++){
            GradeItem it = cmbGrade.getItemAt(i);
            if (it != null && it.name.equals(gradeName)){
                cmbGrade.setSelectedIndex(i);
                return;
            }
        }
    }

    private void loadTable() {
        tableModel.setRowCount(0);
        try {
            List<Employee> list = employeeDAO.getAll();
            for (Employee e : list) {
                tableModel.addRow(new Object[]{
                        e.empId,
                        e.firstName,
                        e.lastName,
                        e.email,
                        e.phone,
                        e.dob,
                        e.hireDate,
                        e.basicSalary,
                        e.deptName,
                        e.gradeName,
                        e.stepNo,
                        e.isActive
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading employees: " + ex.getMessage());
        }
    }

    private void addEmployee() {
        try {
            String first = txtFirstName.getText().trim();
            String last  = txtLastName.getText().trim();
            String email = txtEmail.getText().trim();
            String phone = txtPhone.getText().trim();

            if (first.isEmpty() || last.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "First Name, Last Name, and Email are required.");
                return;
            }

            if (txtHireDate.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Hire Date is required.");
                return;
            }

            Date dob = txtDob.getText().trim().isEmpty() ? null : parseDateOrThrow(txtDob.getText().trim());
            Date hire = parseDateOrThrow(txtHireDate.getText().trim());

            BigDecimal salary;
            try {
                salary = new BigDecimal(txtSalary.getText().trim().isEmpty() ? "0.00" : txtSalary.getText().trim());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Salary must be a number (e.g. 3500.00).");
                return;
            }

            int stepNo;
            try {
                stepNo = Integer.parseInt(txtStepNo.getText().trim().isEmpty() ? "1" : txtStepNo.getText().trim());
                if (stepNo <= 0) {
                    JOptionPane.showMessageDialog(this, "Step No must be a positive number.");
                    return;
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Step No must be a number (e.g. 1).");
                return;
            }

            DeptItem dept = (DeptItem) cmbDept.getSelectedItem();
            if (dept == null) {
                JOptionPane.showMessageDialog(this, "Select a department.");
                return;
            }

            GradeItem grade = (GradeItem) cmbGrade.getSelectedItem();
            if (grade == null) {
                JOptionPane.showMessageDialog(this, "Select a grade.");
                return;
            }

            int active = Integer.parseInt(cmbActive.getSelectedItem().toString());

            boolean ok = employeeDAO.insertWithGrade(first, last, email, phone, dob, hire, salary,
                    dept.id, grade.id, stepNo, active);

            if (ok) {
                JOptionPane.showMessageDialog(this, "✅ Employee added!");
                loadTable();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Failed to add employee.");
            }
        } catch (IllegalArgumentException badDate) {
            JOptionPane.showMessageDialog(this, badDate.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Insert error: " + ex.getMessage());
        }
    }

    private void updateSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a row first.");
            return;
        }

        try {
            int empId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());

            String first = txtFirstName.getText().trim();
            String last  = txtLastName.getText().trim();
            String email = txtEmail.getText().trim();
            String phone = txtPhone.getText().trim();

            if (first.isEmpty() || last.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "First Name, Last Name, and Email are required.");
                return;
            }

            if (txtHireDate.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Hire Date is required.");
                return;
            }

            Date dob = txtDob.getText().trim().isEmpty() ? null : parseDateOrThrow(txtDob.getText().trim());
            Date hire = parseDateOrThrow(txtHireDate.getText().trim());

            BigDecimal salary;
            try {
                salary = new BigDecimal(txtSalary.getText().trim().isEmpty() ? "0.00" : txtSalary.getText().trim());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Salary must be a number (e.g. 3500.00).");
                return;
            }

            int stepNo;
            try {
                stepNo = Integer.parseInt(txtStepNo.getText().trim().isEmpty() ? "1" : txtStepNo.getText().trim());
                if (stepNo <= 0) {
                    JOptionPane.showMessageDialog(this, "Step No must be a positive number.");
                    return;
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Step No must be a number (e.g. 1).");
                return;
            }

            DeptItem dept = (DeptItem) cmbDept.getSelectedItem();
            if (dept == null) {
                JOptionPane.showMessageDialog(this, "Select a department.");
                return;
            }

            GradeItem grade = (GradeItem) cmbGrade.getSelectedItem();
            if (grade == null) {
                JOptionPane.showMessageDialog(this, "Select a grade.");
                return;
            }

            int active = Integer.parseInt(cmbActive.getSelectedItem().toString());

            boolean ok = employeeDAO.updateWithGrade(empId, first, last, email, phone, dob, hire, salary,
                    dept.id, grade.id, stepNo, active);

            if (ok) {
                JOptionPane.showMessageDialog(this, "✅ Updated!");
                loadTable();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Update failed.");
            }
        } catch (IllegalArgumentException badDate) {
            JOptionPane.showMessageDialog(this, badDate.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Update error: " + ex.getMessage());
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a row first.");
            return;
        }

        int empId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete this employee?",
                "Confirm",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (employeeDAO.delete(empId)) {
                    JOptionPane.showMessageDialog(this, "✅ Deleted!");
                    loadTable();
                    clearForm();
                } else {
                    JOptionPane.showMessageDialog(this, "❌ Delete failed.");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Delete error: " + ex.getMessage());
            }
        }
    }

    private void clearForm() {
        txtFirstName.setText("");
        txtLastName.setText("");
        txtEmail.setText("");
        txtPhone.setText("");
        txtDob.setText("");
        txtHireDate.setText("");
        txtSalary.setText("");
        txtStepNo.setText("1");
        cmbActive.setSelectedItem("1");
        if (cmbDept.getItemCount() > 0) cmbDept.setSelectedIndex(0);
        if (cmbGrade.getItemCount() > 0) cmbGrade.setSelectedIndex(0);
        table.clearSelection();
        txtFirstName.requestFocus();
    }

    private static class DeptItem {
        int id;
        String name;
        DeptItem(int id, String name){ this.id = id; this.name = name; }
        @Override public String toString(){ return name; }
    }

    private static class GradeItem {
        int id;
        String name;
        GradeItem(int id, String name){ this.id = id; this.name = name; }
        @Override public String toString(){ return name; }
    }
}