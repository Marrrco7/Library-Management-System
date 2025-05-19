package org.example.gui;

import org.example.model.Borrowing;
import org.example.model.Copy;
import org.example.model.User;
import org.example.service.BorrowingService;
import org.example.service.CopyService;
import org.example.service.UserService;

import javax.swing.*;
import java.awt.*;
import java.util.Date;
import java.util.List;


/**
 * A dialog for adding or editing a borrowing transaction in the library system.
 *
 * <p>This dialog allows the user to select a user and a copy of a book, then
 * specify borrow and return dates, and then create or update a {@link Borrowing}
 * record using the provided services. It uses swing components to create a user interface for input.</p>
 *
 * <p>The dialog interacts with {@link UserService} to populate the list of users,
 * {@link CopyService} to populate the list of available copies, and {@link BorrowingService}
 * to persist borrowing transactions. It updates the copy dropdown and user dropdown
 * based on current data from the database.</p>
 */
public class AddEditBorrowingDialog extends JDialog {

    private JComboBox<String> userDropdown;
    private JComboBox<String> copyDropdown;
    private JSpinner borrowDateSpinner;
    private JSpinner returnDateSpinner;
    private BorrowingService borrowingService;
    private UserService userService;
    private CopyService copyService;
    private int borrowingId = -1;

    public AddEditBorrowingDialog(Frame parent, BorrowingService borrowingService, UserService userService, CopyService copyService) {
        super(parent, "Add/Edit Borrowing", true);
        this.borrowingService = borrowingService;
        this.userService = userService;
        this.copyService = copyService;

        setLayout(new GridLayout(5, 2));

        add(new JLabel("User:"));
        userDropdown = new JComboBox<>();
        populateUserDropdown();
        add(userDropdown);

        add(new JLabel("Copy:"));
        copyDropdown = new JComboBox<>();
        populateCopyDropdown();
        add(copyDropdown);

        add(new JLabel("Borrow Date:"));
        borrowDateSpinner = new JSpinner(new SpinnerDateModel());
        borrowDateSpinner.setEditor(new JSpinner.DateEditor(borrowDateSpinner, "yyyy-MM-dd"));
        add(borrowDateSpinner);

        add(new JLabel("Return Date:"));
        returnDateSpinner = new JSpinner(new SpinnerDateModel());
        returnDateSpinner.setEditor(new JSpinner.DateEditor(returnDateSpinner, "yyyy-MM-dd"));
        add(returnDateSpinner);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> saveBorrowing());
        add(saveButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        add(cancelButton);

        pack();
        setLocationRelativeTo(parent);
    }

    public void setBorrowing(Borrowing borrowing) {
        this.borrowingId = borrowing.getId();
        userDropdown.setSelectedItem(borrowing.getUser().getName());
        copyDropdown.setSelectedItem(String.valueOf(borrowing.getCopy().getId()));
        borrowDateSpinner.setValue(borrowing.getBorrowDate());
        returnDateSpinner.setValue(borrowing.getReturnDate());
    }

    private void populateUserDropdown() {
        List<User> users = userService.getAllUsers();
        for (User user : users) {
            userDropdown.addItem(user.getName());
        }
    }

    private void populateCopyDropdown() {
        List<Copy> copies = copyService.getAvailableCopies();
        for (Copy copy : copies) {
            copyDropdown.addItem(String.valueOf(copy.getId()));
        }
    }

    private void saveBorrowing() {
        String userName = (String) userDropdown.getSelectedItem();
        String copyIdStr = (String) copyDropdown.getSelectedItem();
        Date borrowDate = (Date) borrowDateSpinner.getValue();
        Date returnDate = (Date) returnDateSpinner.getValue();

        if (userName == null || copyIdStr == null) {
            JOptionPane.showMessageDialog(this, "Please select a user and a copy.");
            return;
        }

        int copyId = Integer.parseInt(copyIdStr);
        int userId = userService.getUserByName(userName).getId();

        if (borrowingId == -1) {
            borrowingService.createBorrowing(userId, copyId, borrowDate, returnDate);
            JOptionPane.showMessageDialog(this, "Borrowing added successfully.");
        } else {
            borrowingService.updateBorrowing(borrowingId, borrowDate, returnDate);
            JOptionPane.showMessageDialog(this, "Borrowing updated successfully.");
        }

        dispose();
    }
}
