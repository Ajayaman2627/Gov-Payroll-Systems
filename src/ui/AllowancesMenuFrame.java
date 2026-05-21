package ui;

import javax.swing.*;
import java.awt.*;

public class AllowancesMenuFrame extends JFrame {

    public AllowancesMenuFrame() {
        super("Allowances Module");

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(520, 220);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel title = new JLabel("Allowances Module");
        title.setFont(new Font("Dialog", Font.BOLD, 20));
        root.add(title, BorderLayout.NORTH);

        JPanel buttons = new JPanel(new GridLayout(1, 2, 10, 10));

        JButton typesBtn = new JButton("Allowance Types");
        typesBtn.setFont(new Font("Dialog", Font.BOLD, 16));
        typesBtn.addActionListener(e -> new AllowanceTypesFrame().setVisible(true));

        JButton empBtn = new JButton("Employee Allowances");
        empBtn.setFont(new Font("Dialog", Font.BOLD, 16));
        empBtn.addActionListener(e -> new EmployeeAllowancesFrame().setVisible(true));

        buttons.add(typesBtn);
        buttons.add(empBtn);

        root.add(buttons, BorderLayout.CENTER);

        setContentPane(root);
    }
}