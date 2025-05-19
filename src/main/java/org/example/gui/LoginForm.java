package org.example.gui;

import org.example.model.User;
import org.example.service.UserService;
import org.example.session.SessionManager;

import javax.swing.*;
import java.awt.*;

public class LoginWindow extends JFrame {

    private UserService userService;

    public LoginWindow() {
        userService = new UserService();
        setTitle("Library Login");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(3, 2));

        // Email Field
        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField();

        // Buttons
        JButton loginButton = new JButton("Log In");
        JButton cancelButton = new JButton("Cancel");

        add(emailLabel);
        add(emailField);
        add(loginButton);
        add(cancelButton);

        // Login Action
        loginButton.addActionListener(e -> {
            String email = emailField.getText();
            User user = userService.getUserByEmail(email);
            if (user != null) {
                SessionManager.setLoggedInUser(user);
                JOptionPane.showMessageDialog(this, "Login successful! Welcome, " + user.getName());
                dispose(); // Close the login window
                new MainWindow().setVisible(true); // Open the main window
            } else {
                JOptionPane.showMessageDialog(this, "Invalid email. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Cancel Action
        cancelButton.addActionListener(e -> System.exit(0));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginWindow loginWindow = new LoginWindow();
            loginWindow.setVisible(true);
        });
    }
}
