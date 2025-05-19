package org.example.gui;

import org.example.model.User;
import org.example.service.UserService;
import org.example.session.SessionManager;

import javax.swing.*;
import java.awt.*;


/**
 * The login form for the library management system and where the app should be initialized.
 *
 * <p>This class creates a simple GUI using swing that
 * prompts the user for their email and password. If successful authentication using
 * {@link UserService}, with the logged in user is stored in {@link SessionManager},
 * then the main application window is launched.</p>
 *
 * <p>If the login fails, an error message is displayed. The form also allows
 * cancellation to exit the application.</p>
 */
public class LoginForm extends JFrame {

    private UserService userService;

    public LoginForm() {
        userService = new UserService();
        setTitle("Library Login");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 2));

        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField();
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField();

        JButton loginButton = new JButton("Log In");
        JButton cancelButton = new JButton("Cancel");

        add(emailLabel);
        add(emailField);
        add(passwordLabel);
        add(passwordField);
        add(loginButton);
        add(cancelButton);

        loginButton.addActionListener(e -> {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());

            User user = userService.authenticateUser(email, password);
            if (user != null) {
                SessionManager.setLoggedInUser(user);
                JOptionPane.showMessageDialog(this, "Login successful! Welcome, " + user.getName());
                dispose();
                new MainWindow().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> System.exit(0));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            UserService userService = new UserService();
            if (userService.getUserByEmail("admin@library.com") == null) {
                userService.createUser(
                        "Admin",
                        "admin@library.com",
                        "123456789",
                        "Library HQ",
                        "admin123",
                        "LIBRARIAN"
                );
                System.out.println("Default librarian user created: admin@library.com / admin123");
            }
            LoginForm loginForm = new LoginForm();
            loginForm.setVisible(true);
        });
    }

}
