package ui;

import dao.AllowanceTypeDAO;
import model.AllowanceType;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class AllowanceTypesFrame extends JFrame {

    private final AllowanceTypeDAO dao = new AllowanceTypeDAO();
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Name", "Description", "Active"}, 0
    ) {
        @Override public boolean isCellEditable(int row, int col) { return false; }
    };
    private final JTable table = new JTable(model);

    public AllowanceTypesFrame() {
        super("Allowance Types");

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 420);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Allowance Types");
        title.setFont(new Font("Dialog", Font.BOLD, 18));
        root.add(title, BorderLayout.NORTH);

        table.setRowHeight(26);
        root.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(e -> load());

        JButton add = new JButton("Add");
        add.addActionListener(e -> onAdd());

        JButton edit = new JButton("Edit");
        edit.addActionListener(e -> onEdit());

        JButton deactivate = new JButton("Deactivate");
        deactivate.addActionListener(e -> onDeactivate());

        actions.add(refresh);
        actions.add(add);
        actions.add(edit);
        actions.add(deactivate);

        root.add(actions, BorderLayout.SOUTH);

        setContentPane(root);
        load();
    }

    private void load() {
        model.setRowCount(0);
        try {
            List<AllowanceType> list = dao.getAll(false);
            for (AllowanceType t : list) {
                model.addRow(new Object[]{
                        t.getAllowanceTypeId(), t.getName(), t.getDescription(), t.isActive()
                });
            }
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void onAdd() {
        AllowanceTypeFormDialog dlg = new AllowanceTypeFormDialog(this, "Add Allowance Type", null);
        dlg.setVisible(true);
        if (!dlg.isSaved()) return;

        try {
            dao.insert(dlg.getValue());
            load();
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

        AllowanceType t = new AllowanceType(
                (int) model.getValueAt(row, 0),
                (String) model.getValueAt(row, 1),
                (String) model.getValueAt(row, 2),
                (boolean) model.getValueAt(row, 3)
        );

        AllowanceTypeFormDialog dlg = new AllowanceTypeFormDialog(this, "Edit Allowance Type", t);
        dlg.setVisible(true);
        if (!dlg.isSaved()) return;

        try {
            dao.update(dlg.getValue());
            load();
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void onDeactivate() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a row first.");
            return;
        }
        int id = (int) model.getValueAt(row, 0);
        String name = String.valueOf(model.getValueAt(row, 1));

        int ok = JOptionPane.showConfirmDialog(this,
                "Deactivate allowance type: " + name + " ?",
                "Confirm", JOptionPane.YES_NO_OPTION);

        if (ok != JOptionPane.YES_OPTION) return;

        try {
            dao.softDelete(id);
            load();
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void showError(Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}