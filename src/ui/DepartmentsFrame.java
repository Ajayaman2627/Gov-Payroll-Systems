package ui;

import dao.DepartmentDAO;
import model.Department;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DepartmentsFrame extends JFrame {

    private final DepartmentDAO dao = new DepartmentDAO();

    private final JTextField txtName = new JTextField(20);
    private final JTextField txtCode = new JTextField(10);
    private final JComboBox<String> cmbActive = new JComboBox<>(new String[]{"1", "0"});

    // Make table NOT editable
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"ID", "Name", "Code", "Active"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable table = new JTable(tableModel);

    public DepartmentsFrame() {
        setTitle("Departments");
        setSize(900, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // -------- FORM PANEL --------
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.fill = GridBagConstraints.HORIZONTAL;

        g.gridx = 0; g.gridy = 0;
        form.add(new JLabel("Department Name:"), g);
        g.gridx = 1;
        form.add(txtName, g);

        g.gridx = 0; g.gridy = 1;
        form.add(new JLabel("Department Code:"), g);
        g.gridx = 1;
        form.add(txtCode, g);

        g.gridx = 0; g.gridy = 2;
        form.add(new JLabel("Active (1/0):"), g);
        g.gridx = 1;
        form.add(cmbActive, g);

        // -------- BUTTONS --------
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

        btnAdd.addActionListener(e -> addDepartment());
        btnUpdate.addActionListener(e -> updateSelected());
        btnDelete.addActionListener(e -> deleteSelected());
        btnRefresh.addActionListener(e -> loadTable());
        btnClear.addActionListener(e -> clearForm());

        // -------- TABLE --------
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // When user clicks a row, populate form fields
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                int row = table.getSelectedRow();
                txtName.setText(tableModel.getValueAt(row, 1).toString());
                txtCode.setText(tableModel.getValueAt(row, 2).toString());
                cmbActive.setSelectedItem(tableModel.getValueAt(row, 3).toString());
            }
        });

        JScrollPane scroll = new JScrollPane(table);

        // -------- LAYOUT --------
        JPanel top = new JPanel(new BorderLayout());
        top.add(form, BorderLayout.CENTER);
        top.add(buttons, BorderLayout.SOUTH);

        add(top, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        // Load data initially
        loadTable();
    }

    private void loadTable() {
        tableModel.setRowCount(0);

        try {
            // DAO must return List<Department>
            List<Department> list = dao.getAll();

            if (list == null) {
                JOptionPane.showMessageDialog(this, "No data returned from database.");
                return;
            }

            for (Department d : list) {
                tableModel.addRow(new Object[]{
                        d.getId(),
                        d.getName(),
                        d.getCode(),
                        d.getIsActive()
                });
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading departments: " + ex.getMessage());
        }
    }

    private void addDepartment() {
        String name = txtName.getText().trim();
        String code = txtCode.getText().trim();
        int active = Integer.parseInt(cmbActive.getSelectedItem().toString());

        if (name.isEmpty() || code.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name and Code are required.");
            return;
        }

        try {
            if (dao.insert(name, code, active)) {
                JOptionPane.showMessageDialog(this, "✅ Department added!");
                loadTable();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Failed to add (maybe duplicate name/code).");
            }
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

        String name = txtName.getText().trim();
        String code = txtCode.getText().trim();
        if (name.isEmpty() || code.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name and Code are required.");
            return;
        }

        int id = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
        int active = Integer.parseInt(cmbActive.getSelectedItem().toString());

        try {
            if (dao.update(id, name, code, active)) {
                JOptionPane.showMessageDialog(this, "✅ Updated!");
                loadTable();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Update failed.");
            }
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

        int id = Integer.parseInt(tableModel.getValueAt(row, 0).toString());

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete this department?",
                "Confirm",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (dao.delete(id)) {
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
        txtName.setText("");
        txtCode.setText("");
        cmbActive.setSelectedItem("1");
        table.clearSelection();
        txtName.requestFocus();
    }
}
