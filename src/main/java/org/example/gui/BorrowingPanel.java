package org.example.gui;

import org.example.model.Borrowing;
import org.example.service.BorrowingService;
import org.example.service.CopyService;
import org.example.service.UserService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;


/**
 * Panel that displays and manages all borrowing transactions.
 *
 * <p>This panel uses a table to display all borrowings with details
 * such as Borrowing ID, User name, Copy ID, Borrow Date, and Return Date.
 * It interacts with {@link BorrowingService}, {@link UserService}, and
 * {@link CopyService} to load and manage borrowing records.</p>
 *
 * <p>Users can add, edit, or delete borrowings using the provided buttons.
 * The panel updates the table data accordingly after each operation.</p>
 */
public class BorrowingPanel extends JPanel {

    private BorrowingService borrowingService;
    private UserService userService;
    private CopyService copyService;
    private JTable borrowingTable;
    private DefaultTableModel tableModel;

    public BorrowingPanel() {
        borrowingService = new BorrowingService();
        userService = new UserService();
        copyService = new CopyService();

        setLayout(new BorderLayout());


        String[] columnNames = {"ID", "User", "Copy ID", "Borrow Date", "Return Date"};
        tableModel = new DefaultTableModel(columnNames, 0);
        borrowingTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(borrowingTable);
        add(scrollPane, BorderLayout.CENTER);


        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Borrowing");
        JButton editButton = new JButton("Edit Borrowing");
        JButton deleteButton = new JButton("Delete Borrowing");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);

        loadBorrowings();

        addButton.addActionListener(e -> openAddDialog());
        editButton.addActionListener(e -> openEditDialog());
        deleteButton.addActionListener(e -> deleteBorrowing());
    }

    private void loadBorrowings() {
        tableModel.setRowCount(0);
        List<Borrowing> borrowings = borrowingService.getAllBorrowings();
        for (Borrowing borrowing : borrowings) {
            tableModel.addRow(new Object[]{
                    borrowing.getId(),
                    borrowing.getUser().getName(),
                    borrowing.getCopy().getId(),
                    borrowing.getBorrowDate(),
                    borrowing.getReturnDate()
            });
        }
    }

    private void openAddDialog() {
        AddEditBorrowingDialog dialog = new AddEditBorrowingDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                borrowingService,
                userService,
                copyService
        );
        dialog.setVisible(true);
        loadBorrowings();
    }

    private void openEditDialog() {
        int selectedRow = borrowingTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a borrowing to edit.");
            return;
        }

        int borrowingId = (int) tableModel.getValueAt(selectedRow, 0);
        Borrowing borrowing = borrowingService.getBorrowingById(borrowingId);

        AddEditBorrowingDialog dialog = new AddEditBorrowingDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                borrowingService,
                userService,
                copyService
        );
        dialog.setBorrowing(borrowing);
        dialog.setVisible(true);
        loadBorrowings();
    }

    private void deleteBorrowing() {
        int selectedRow = borrowingTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a borrowing to delete.");
            return;
        }

        int borrowingId = (int) tableModel.getValueAt(selectedRow, 0);
        int confirmation = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this borrowing?",
                "Delete Borrowing",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmation == JOptionPane.YES_OPTION) {
            borrowingService.deleteBorrowing(borrowingId);
            loadBorrowings();
        }
    }
}
