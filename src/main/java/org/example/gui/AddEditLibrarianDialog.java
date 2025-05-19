package org.example.gui;

import org.example.model.User;
import org.example.service.LibrarianService;
import org.example.service.UserService;

import javax.swing.*;
import java.awt.*;
import java.util.Date;
import java.util.List;


/**
 * A modal dialog for adding or editing librarian records in the library system.
 *
 * <p>This dialog allows selection of a user to be promoted to or updated as a librarian,
 * setting their position and employment date. It interacts with  the {@link LibrarianService}
 * to create or update librarian records and with {@link UserService} in order to manage user role updates.</p>
 *
 * <p>The dialog presents fields for selecting a user, entering a librarian position,
 * and choosing an employment date. It updates the user's role to "LIBRARIAN" if necessary
 * before creating a librarian record.</p>
 */

public class AddEditLibrarianDialog extends JDialog {

    private JComboBox<String> userDropdown;
    private JTextField positionField;
    private JSpinner employmentDateSpinner;
    private LibrarianService librarianService;
    private UserService userService;
    private int librarianId = -1;

    public AddEditLibrarianDialog(Frame parent, LibrarianService librarianService, UserService userService) {
        super(parent, "Add/Edit Librarian", true);
        this.librarianService = librarianService;
        this.userService = userService;

        setLayout(new GridLayout(4, 2));


        add(new JLabel("User:"));
        userDropdown = new JComboBox<>();
        populateUserDropdown();
        add(userDropdown);


        add(new JLabel("Position:"));
        positionField = new JTextField();
        add(positionField);


        add(new JLabel("Employment Date:"));
        employmentDateSpinner = new JSpinner(new SpinnerDateModel());
        employmentDateSpinner.setEditor(new JSpinner.DateEditor(employmentDateSpinner, "yyyy-MM-dd"));
        add(employmentDateSpinner);


        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> saveLibrarian());
        add(saveButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        add(cancelButton);

        pack();
        setLocationRelativeTo(parent);
    }

    private void populateUserDropdown() {
        List<User> users = userService.getAllUsers();
        for (User user : users) {
            userDropdown.addItem(user.getName());
        }
    }

    private void saveLibrarian() {
        String userName = (String) userDropdown.getSelectedItem();
        String position = positionField.getText();
        Date employmentDate = (Date) employmentDateSpinner.getValue();

        User selectedUser = userService.getUserByName(userName);
        if (selectedUser == null) {
            JOptionPane.showMessageDialog(this, "Invalid user selected.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }


        if (!"LIBRARIAN".equals(selectedUser.getRole())) {

            selectedUser.setRole("LIBRARIAN");
            userService.updateUser(
                    selectedUser.getId(),
                    selectedUser.getName(),
                    selectedUser.getEmail(),
                    selectedUser.getPhoneNumber(),
                    selectedUser.getAddress(),
                    selectedUser.getPassword(),
                    "LIBRARIAN"
            );
        }

        if (librarianId == -1) {
            librarianService.createLibrarian(selectedUser.getId(), position, employmentDate);
            JOptionPane.showMessageDialog(this, "Librarian added successfully.");
        } else {
            librarianService.updateLibrarian(librarianId, position, employmentDate);
            JOptionPane.showMessageDialog(this, "Librarian updated successfully.");
        }

        dispose();
    }


    public void setLibrarian(int librarianId, int userId, String position, Date employmentDate) {
        this.librarianId = librarianId;
        userDropdown.setSelectedItem(userService.getUserById(userId).getName());
        positionField.setText(position);
        employmentDateSpinner.setValue(employmentDate);
    }
}
