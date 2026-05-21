package ui;

import model.AllowanceCalcMode;
import model.AllowanceType;
import model.EmployeeAllowance;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class EmployeeAllowanceFormDialog extends JDialog {

    private boolean saved = false;

    private final JComboBox<AllowanceType> typeBox = new JComboBox<>();
    private final JRadioButton amountRadio = new JRadioButton("Amount");
    private final JRadioButton percentRadio = new JRadioButton("Percent of Basic");

    private final JTextField amountField = new JTextField();
    private final JTextField percentField = new JTextField();

    private final JTextField effectiveFromField = new JTextField(); // yyyy-mm-dd
    private final JTextField effectiveToField = new JTextField();   // yyyy-mm-dd

    private EmployeeAllowance value;

    public EmployeeAllowanceFormDialog(Frame owner, String title, EmployeeAllowance existing, List<AllowanceType> types) {
        super(owner, title, true);

        setSize(650, 340);
        setLocationRelativeTo(owner);

        for (AllowanceType t : types) typeBox.addItem(t);

        ButtonGroup g = new ButtonGroup();
        g.add(amountRadio);
        g.add(percentRadio);

        amountRadio.addActionListener(e -> toggleMode());
        percentRadio.addActionListener(e -> toggleMode());

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel form = new JPanel(new GridLayout(6, 2, 10, 10));

        form.add(new JLabel("Allowance Type:"));
        form.add(typeBox);

        form.add(new JLabel("Calculation:"));
        JPanel radios = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        radios.add(amountRadio);
        radios.add(percentRadio);
        form.add(radios);

        form.add(new JLabel("Amount (if Amount mode):"));
        form.add(amountField);

        form.add(new JLabel("Percent (0-100) (if Percent mode):"));
        form.add(percentField);

        form.add(new JLabel("Effective From (yyyy-mm-dd) optional:"));
        form.add(effectiveFromField);

        form.add(new JLabel("Effective To (yyyy-mm-dd) optional:"));
        form.add(effectiveToField);

        root.add(form, BorderLayout.CENTER);

        JButton saveBtn = new JButton("Save");
        saveBtn.addActionListener(e -> onSave());

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dispose());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(cancelBtn);
        buttons.add(saveBtn);

        root.add(buttons, BorderLayout.SOUTH);

        // preload
        if (existing != null) {
            this.value = existing;

            // select type by id
            for (int i = 0; i < typeBox.getItemCount(); i++) {
                if (typeBox.getItemAt(i).getAllowanceTypeId() == existing.getAllowanceTypeId()) {
                    typeBox.setSelectedIndex(i);
                    break;
                }
            }

            if (existing.getCalcMode() == AllowanceCalcMode.AMOUNT) amountRadio.setSelected(true);
            else percentRadio.setSelected(true);

            amountField.setText(existing.getAmount() == null ? "" : existing.getAmount().toPlainString());
            percentField.setText(existing.getPercent() == null ? "" : existing.getPercent().toPlainString());

            effectiveFromField.setText(existing.getEffectiveFrom() == null ? "" : existing.getEffectiveFrom().toString());
            effectiveToField.setText(existing.getEffectiveTo() == null ? "" : existing.getEffectiveTo().toString());
        } else {
            amountRadio.setSelected(true);
        }

        toggleMode();
        setContentPane(root);
    }

    private void toggleMode() {
        boolean amountMode = amountRadio.isSelected();
        amountField.setEnabled(amountMode);
        percentField.setEnabled(!amountMode);
    }

    private void onSave() {
        AllowanceType t = (AllowanceType) typeBox.getSelectedItem();
        if (t == null) {
            JOptionPane.showMessageDialog(this, "Select allowance type.");
            return;
        }

        AllowanceCalcMode mode = amountRadio.isSelected() ? AllowanceCalcMode.AMOUNT : AllowanceCalcMode.PERCENT;

        BigDecimal amount = null;
        BigDecimal percent = null;

        try {
            if (mode == AllowanceCalcMode.AMOUNT) {
                String s = amountField.getText().trim();
                if (s.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Amount is required.");
                    return;
                }
                amount = new BigDecimal(s);
                if (amount.compareTo(BigDecimal.ZERO) < 0) {
                    JOptionPane.showMessageDialog(this, "Amount must be >= 0");
                    return;
                }
            } else {
                String s = percentField.getText().trim();
                if (s.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Percent is required.");
                    return;
                }
                percent = new BigDecimal(s);
                if (percent.compareTo(BigDecimal.ZERO) < 0 || percent.compareTo(new BigDecimal("100")) > 0) {
                    JOptionPane.showMessageDialog(this, "Percent must be between 0 and 100.");
                    return;
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid number format.");
            return;
        }

        LocalDate ef = null;
        LocalDate et = null;
        try {
            String s1 = effectiveFromField.getText().trim();
            String s2 = effectiveToField.getText().trim();
            if (!s1.isEmpty()) ef = LocalDate.parse(s1);
            if (!s2.isEmpty()) et = LocalDate.parse(s2);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Effective dates must be yyyy-mm-dd");
            return;
        }

        if (value == null) value = new EmployeeAllowance();

        value.setAllowanceTypeId(t.getAllowanceTypeId());
        value.setAllowanceTypeName(t.getName());
        value.setCalcMode(mode);
        value.setAmount(amount);
        value.setPercent(percent);
        value.setEffectiveFrom(ef);
        value.setEffectiveTo(et);

        saved = true;
        dispose();
    }

    public boolean isSaved() { return saved; }
    public EmployeeAllowance getValue() { return value; }
}