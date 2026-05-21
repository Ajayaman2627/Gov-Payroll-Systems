package ui;

import javax.swing.*;
import java.awt.*;

public class DeductionsFrame extends JFrame {

    public DeductionsFrame() {
        setTitle("Deductions");
        setSize(820, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Deduction Types",      new ManageDeductionTypePanel());
        tabs.addTab("Employee Deductions",  new ManageEmployeeDeductionPanel());

        JLabel header = new JLabel("Deductions Module", SwingConstants.CENTER);
        header.setFont(new Font("Dialog", Font.BOLD, 18));
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 6, 10));

        add(header, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
    }
}
