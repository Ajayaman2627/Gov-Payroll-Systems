package ui;

import dao.GradeDAO;
import dao.PayStructureDAO;
import model.Grade;
import model.PayStructure;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class PayStructureFrame extends JFrame {

    private final PayStructureDAO dao = new PayStructureDAO();
    private final GradeDAO gradeDAO = new GradeDAO();

    private final JComboBox<GradeItem> cmbGrade = new JComboBox<>();
    private final JSpinner spnStep = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));
    private final JTextField txtSalary = new JTextField(12);

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"ID", "Grade", "Step", "Salary Amount (GHS)"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int col) { return false; }
    };
    private final JTable table = new JTable(tableModel);

    public PayStructureFrame() {
        setTitle("Pay Structure — Grade / Step Salary Matrix");
        setSize(720, 540);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Form
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Add / Edit Pay Structure Entry"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(7, 7, 7, 7);
        g.fill = GridBagConstraints.HORIZONTAL;

        g.gridx = 0; g.gridy = 0; form.add(new JLabel("Grade:"), g);
        g.gridx = 1; form.add(cmbGrade, g);
        g.gridx = 2; form.add(new JLabel("Step No:"), g);
        g.gridx = 3; form.add(spnStep, g);
        g.gridx = 0; g.gridy = 1; form.add(new JLabel("Salary Amount:"), g);
        g.gridx = 1; form.add(txtSalary, g);

        // Buttons
        JButton btnAdd    = new JButton("Add");
        JButton btnUpdate = new JButton("Update Selected");
        JButton btnDelete = new JButton("Delete Selected");
        JButton btnRefresh = new JButton("Refresh");
        JButton btnClear  = new JButton("Clear");

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        buttons.add(btnAdd);
        buttons.add(btnUpdate);
        buttons.add(btnDelete);
        buttons.add(btnRefresh);
        buttons.add(btnClear);

        btnAdd.addActionListener(e -> add());
        btnUpdate.addActionListener(e -> update());
        btnDelete.addActionListener(e -> delete());
        btnRefresh.addActionListener(e -> loadTable());
        btnClear.addActionListener(e -> clearForm());

        // Table
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(22);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                int row = table.getSelectedRow();
                selectGradeByName(tableModel.getValueAt(row, 1).toString());
                spnStep.setValue(Integer.parseInt(tableModel.getValueAt(row, 2).toString()));
                txtSalary.setText(tableModel.getValueAt(row, 3).toString());
            }
        });

        JPanel top = new JPanel(new BorderLayout());
        top.add(form, BorderLayout.CENTER);
        top.add(buttons, BorderLayout.SOUTH);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadGrades();
        loadTable();
    }

    private void loadGrades() {
        cmbGrade.removeAllItems();
        try {
            for (Grade gr : gradeDAO.getAll())
                cmbGrade.addItem(new GradeItem(gr.getId(), gr.getName()));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading grades: " + ex.getMessage());
        }
    }

    private void selectGradeByName(String name) {
        for (int i = 0; i < cmbGrade.getItemCount(); i++) {
            if (cmbGrade.getItemAt(i).name.equals(name)) {
                cmbGrade.setSelectedIndex(i);
                return;
            }
        }
    }

    private void loadTable() {
        tableModel.setRowCount(0);
        try {
            for (PayStructure ps : dao.getAll()) {
                tableModel.addRow(new Object[]{
                    ps.getId(), ps.getGradeName(), ps.getStepNo(),
                    String.format("%,.2f", ps.getSalaryAmount())
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading pay structure: " + ex.getMessage() +
                "\n\nMake sure the pay_structure table exists in your database.");
        }
    }

    private void add() {
        GradeItem grade = (GradeItem) cmbGrade.getSelectedItem();
        if (grade == null) { JOptionPane.showMessageDialog(this, "Select a grade."); return; }
        String salStr = txtSalary.getText().trim();
        if (salStr.isEmpty()) { JOptionPane.showMessageDialog(this, "Enter a salary amount."); return; }
        try {
            BigDecimal salary = new BigDecimal(salStr);
            int step = (Integer) spnStep.getValue();
            if (dao.insert(grade.id, step, salary)) {
                JOptionPane.showMessageDialog(this, "Entry added.");
                loadTable();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add (duplicate grade/step combination?).");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Salary must be a valid number (e.g. 3500.00).");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void update() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a row first."); return; }
        int id = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
        String salStr = txtSalary.getText().trim();
        if (salStr.isEmpty()) { JOptionPane.showMessageDialog(this, "Enter a salary amount."); return; }
        try {
            BigDecimal salary = new BigDecimal(salStr.replace(",", ""));
            if (dao.update(id, salary)) {
                JOptionPane.showMessageDialog(this, "Updated.");
                loadTable();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Update failed.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void delete() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a row first."); return; }
        int id = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this pay structure entry?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (dao.delete(id)) { JOptionPane.showMessageDialog(this, "Deleted."); loadTable(); clearForm(); }
                else JOptionPane.showMessageDialog(this, "Delete failed.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    private void clearForm() {
        if (cmbGrade.getItemCount() > 0) cmbGrade.setSelectedIndex(0);
        spnStep.setValue(1);
        txtSalary.setText("");
        table.clearSelection();
        txtSalary.requestFocus();
    }

    private static class GradeItem {
        final int id;
        final String name;
        GradeItem(int id, String name) { this.id = id; this.name = name; }
        @Override public String toString() { return name; }
    }
}
