package ui;

import dao.DeductionTypeDAO;
import model.DeductionType;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class ManageDeductionTypePanel extends JPanel {

    private JTextField txtDeductionName;
    private JTable table;
    private DefaultTableModel model;

    private final DeductionTypeDAO deductionTypeDAO = new DeductionTypeDAO();
    private int selectedId = -1;

    public ManageDeductionTypePanel() {
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        topPanel.setBorder(BorderFactory.createTitledBorder("Deduction Type Details"));

        topPanel.add(new JLabel("Deduction Name:"));
        txtDeductionName = new JTextField();
        topPanel.add(txtDeductionName);

        JButton btnAdd = new JButton("Add");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");
        JButton btnClear = new JButton("Clear");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);

        model = new DefaultTableModel(new String[]{"ID", "Deduction Name"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        loadDeductionTypes();

        btnAdd.addActionListener(e -> addDeductionType());
        btnUpdate.addActionListener(e -> updateDeductionType());
        btnDelete.addActionListener(e -> deleteDeductionType());
        btnClear.addActionListener(e -> clearForm());

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int row = table.getSelectedRow();
                selectedId = Integer.parseInt(model.getValueAt(row, 0).toString());
                txtDeductionName.setText(model.getValueAt(row, 1).toString());
            }
        });
    }

    private void loadDeductionTypes() {
        model.setRowCount(0);

        try {
            List<DeductionType> list = deductionTypeDAO.getAllDeductionTypes();
            for (DeductionType d : list) {
                model.addRow(new Object[]{
                        d.getDeductionTypeId(),
                        d.getDeductionName()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading deduction types: " + ex.getMessage());
        }
    }

    private void addDeductionType() {
        String name = txtDeductionName.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Deduction name is required.");
            return;
        }

        try {
            deductionTypeDAO.addDeductionType(new DeductionType(0, name));
            JOptionPane.showMessageDialog(this, "Deduction type added successfully.");
            clearForm();
            loadDeductionTypes();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error adding deduction type: " + ex.getMessage());
        }
    }

    private void updateDeductionType() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Select a row to update.");
            return;
        }

        String name = txtDeductionName.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Deduction name is required.");
            return;
        }

        try {
            deductionTypeDAO.updateDeductionType(new DeductionType(selectedId, name));
            JOptionPane.showMessageDialog(this, "Deduction type updated successfully.");
            clearForm();
            loadDeductionTypes();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating deduction type: " + ex.getMessage());
        }
    }

    private void deleteDeductionType() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Select a row to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this deduction type?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            deductionTypeDAO.deleteDeductionType(selectedId);
            JOptionPane.showMessageDialog(this, "Deduction type deleted successfully.");
            clearForm();
            loadDeductionTypes();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error deleting deduction type: " + ex.getMessage());
        }
    }

    private void clearForm() {
        txtDeductionName.setText("");
        selectedId = -1;
        table.clearSelection();
    }
}