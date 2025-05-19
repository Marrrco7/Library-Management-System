package org.example.gui;

import org.example.model.User;
import org.example.service.LibrarianService;
import org.example.service.UserService;


import javax.swing.*;
import java.awt.*;
import java.util.Date;


/**
 * For adding or editing user accounts in the library management system.
 *
 * <p>This dialog provides fields for entering user details such as name, email, phone,
 * address, password, and role. It interacts with {@link UserService} to create or update
 * user records and with {@link LibrarianService} to handle librarian specific logic when
 * a user's role changes to librarian.</p>
 *
 * <p>Depending on whether a new user is being created or an existing user is edited,
 * the dialog calls the appropriate service methods in order to persist the changes.</p>
 */
public class AddEditUserDialog extends JDialog {

    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextField addressField;
    private JComboBox<String> roleDropdown;

    private LibrarianService librarianService;
    private JPasswordField passwordField;
    private UserService userService;
    private int userId = -1;

    public AddEditUserDialog(Frame parent, UserService userService, LibrarianService librarianService) {
        super(parent, "Add/Edit User", true);
        this.userService = userService;

        this.librarianService = librarianService;
        setLayout(new GridLayout(7, 2));


        add(new JLabel("Name:"));
        nameField = new JTextField();
        add(nameField);

        add(new JLabel("Email:"));
        emailField = new JTextField();
        add(emailField);

        add(new JLabel("Phone:"));
        phoneField = new JTextField();
        add(phoneField);

        add(new JLabel("Address:"));
        addressField = new JTextField();
        add(addressField);

        add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        add(passwordField);

        roleDropdown = new JComboBox<>(new String[]{"USER", "LIBRARIAN"});
        add(new JLabel("Role:"));
        add(roleDropdown);


        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> saveUser());
        add(saveButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        add(cancelButton);

        pack();
        setLocationRelativeTo(parent);
    }

    public void setUser(int userId, String name, String email, String phone, String address) {
        this.userId = userId;
        nameField.setText(name);
        emailField.setText(email);
        phoneField.setText(phone);
        addressField.setText(address);
    }



    private void saveUser() {
        String name = nameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String address = addressField.getText();
        String password = new String(passwordField.getPassword());
        String role = (String) roleDropdown.getSelectedItem();

        if (userId == -1) {
            userService.createUser(name, email, phone, address, password, role);

            if ("LIBRARIAN".equals(role)) {
                User newUser = userService.getUserByEmail(email);
                librarianService.createLibrarian(newUser.getId(), "Default Position", new Date());
            }

            JOptionPane.showMessageDialog(this, "User added successfully.");
        } else {
            userService.updateUser(userId, name, email, phone, address, password, role);

            if ("LIBRARIAN".equals(role)) {
                if (librarianService.getLibrarianById(userId) == null) {
                    librarianService.createLibrarian(userId, "Default Position", new Date());
                }
            } else {

                librarianService.deleteLibrarian(userId);
            }

            JOptionPane.showMessageDialog(this, "User updated successfully.");
        }

        dispose();
    }



}
