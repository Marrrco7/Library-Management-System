package org.example.gui;

import org.example.model.User;
import org.example.service.LibrarianService;
import org.example.service.UserService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel for displaying and managing user accounts.
 *
 * <p>This panel shows a table of users including details such as ID, name, email,
 * phone number, address, and role. It provides buttons to add, edit, and delete
 * users, interacting with {@link UserService} for user operations and
 * {@link LibrarianService} to handle related librarian data when needed.</p>
 *
 * <p>This panel loads all the user data into a table on initialization and after any
 * add, edit, or delete action, allowing librarians to manage user information
 * through the GUI.</p>
 */
public class UserPanel extends JPanel {

    private UserService userService;
    private JTable userTable;
    private LibrarianService librarianService;
    private DefaultTableModel tableModel;

    public UserPanel() {
        userService = new UserService();
        librarianService = new LibrarianService();
        setLayout(new BorderLayout());


        String[] columnNames = {"ID", "Name", "Email", "Phone", "Address"};
        tableModel = new DefaultTableModel(columnNames, 0);
        userTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(userTable);


        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add User");
        JButton editButton = new JButton("Edit User");
        JButton deleteButton = new JButton("Delete User");
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);


        loadData();

        addButton.addActionListener(e -> showAddUserDialog());
        editButton.addActionListener(e -> showEditUserDialog());
        deleteButton.addActionListener(e -> deleteSelectedUser());
    }

    private void loadData() {
        List<User> users = userService.getAllUsers();
        tableModel.setRowCount(0);
        for (User user : users) {
            tableModel.addRow(new Object[]{
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getPhoneNumber(),
                    user.getAddress(),
                    user.getRole()
            });
        }
    }


    private void showAddUserDialog() {
        AddEditUserDialog dialog = new AddEditUserDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                userService,
                librarianService
        );
        dialog.setVisible(true);
        loadData();
    }


    private void showEditUserDialog() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to edit.");
            return;
        }


        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        User user = userService.getUserById(userId);


        AddEditUserDialog dialog = new AddEditUserDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                userService,
                librarianService
        );
        dialog.setUser(user.getId(), user.getName(), user.getEmail(), user.getPhoneNumber(), user.getAddress());
        dialog.setVisible(true);


        loadData();
    }


    private void deleteSelectedUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow != -1) {
            int userId = (int) tableModel.getValueAt(selectedRow, 0);
            librarianService.deleteLibrarian(userId);

            userService.deleteUser(userId);
            loadData();
            JOptionPane.showMessageDialog(this, "User deleted successfully.");
        } else {
            JOptionPane.showMessageDialog(this, "Please select a user to delete.");
        }
    }

}
