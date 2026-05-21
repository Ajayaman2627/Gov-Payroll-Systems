package ui;

import app.Session;
import dao.UserDAO;
import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private final JTextField txtUsername = new JTextField(20);
    private final JPasswordField txtPassword = new JPasswordField(20);
    private final JLabel lblStatus = new JLabel(" ");

    public LoginFrame() {
        setTitle("Government Payroll System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 220);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 8, 8, 8);
        g.fill = GridBagConstraints.HORIZONTAL;

        g.gridx = 0; g.gridy = 0;
        panel.add(new JLabel("Username:"), g);
        g.gridx = 1;
        panel.add(txtUsername, g);

        g.gridx = 0; g.gridy = 1;
        panel.add(new JLabel("Password:"), g);
        g.gridx = 1;
        panel.add(txtPassword, g);

        JButton btnLogin = new JButton("Login");
        btnLogin.addActionListener(e -> doLogin());

        g.gridx = 1; g.gridy = 2;
        panel.add(btnLogin, g);

        lblStatus.setForeground(Color.RED);
        g.gridx = 1; g.gridy = 3;
        panel.add(lblStatus, g);

        add(panel);
    }

    private void doLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            lblStatus.setText("Enter username and password.");
            return;
        }

        UserDAO dao = new UserDAO();
        UserDAO.LoginResult result = dao.login(username, password);

        if (result == null) {
            lblStatus.setText("Invalid login. Try again.");
            return;
        }

        Session.userId = result.userId;
        Session.username = result.username;
        Session.roleName = result.roleName;

        JOptionPane.showMessageDialog(this,
                "Welcome " + Session.username + " (" + Session.roleName + ")");
                
new DashboardFrame().setVisible(true);
dispose();
        
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
