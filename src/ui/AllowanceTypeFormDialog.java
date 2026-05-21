package ui;

import model.AllowanceType;

import javax.swing.*;
import java.awt.*;

public class AllowanceTypeFormDialog extends JDialog {

    private boolean saved = false;

    private final JTextField nameField = new JTextField();
    private final JTextField descField = new JTextField();
    private final JCheckBox activeBox = new JCheckBox("Active", true);

    private AllowanceType value;

    public AllowanceTypeFormDialog(Frame owner, String title, AllowanceType existing) {
        super(owner, title, true);

        setSize(520, 220);
        setLocationRelativeTo(owner);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel form = new JPanel(new GridLayout(3, 2, 10, 10));
        form.add(new JLabel("Name:"));
        form.add(nameField);
        form.add(new JLabel("Description:"));
        form.add(descField);
        form.add(new JLabel(""));
        form.add(activeBox);

        root.add(form, BorderLayout.CENTER);

        JButton saveBtn = new JButton("Save");
        saveBtn.addActionListener(e -> onSave());

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dispose());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(cancelBtn);
        buttons.add(saveBtn);

        root.add(buttons, BorderLayout.SOUTH);

        if (existing != null) {
            nameField.setText(existing.getName());
            descField.setText(existing.getDescription());
            activeBox.setSelected(existing.isActive());
            this.value = existing;
        }

        setContentPane(root);
    }

    private void onSave() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name is required.");
            return;
        }

        if (value == null) value = new AllowanceType();

        value.setName(name);
        value.setDescription(descField.getText().trim());
        value.setActive(activeBox.isSelected());

        saved = true;
        dispose();
    }

    public boolean isSaved() { return saved; }
    public AllowanceType getValue() { return value; }
}