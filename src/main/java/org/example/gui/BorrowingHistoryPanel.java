package org.example.gui;

import org.example.model.Borrowing;
import org.example.model.User;
import org.example.service.BorrowingService;
import org.example.session.SessionManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;


/**
 * Panel that displays the borrowing history for the current user that is logged in.
 *
 * <p>This panel checks if a user is logged in using {@link SessionManager}.
 * If no user is logged in, it prompts the user to log in. Otherwise, it retrieves
 * the borrowing history for the logged in user through {@link BorrowingService}
 * and displays it.</p>
 *
 * <p>Columns displayed include Borrowing ID, Book Title, Borrow Date,
 * and Return Date. It updates the table with the user's borrowing history
 * upon initialization.</p>
 */
public class BorrowingHistoryPanel extends JPanel {

    private BorrowingService borrowingService;
    private DefaultTableModel tableModel;

    public BorrowingHistoryPanel() {
        borrowingService = new BorrowingService();
        setLayout(new BorderLayout());

        if (!SessionManager.isLoggedIn()) {
            add(new JLabel("Please log in first.", SwingConstants.CENTER), BorderLayout.CENTER);
            return;
        }

        String[] columnNames = {"ID", "Title", "Borrow Date", "Return Date"};
        tableModel = new DefaultTableModel(columnNames, 0);
        JTable historyTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(historyTable);

        add(scrollPane, BorderLayout.CENTER);


        loadBorrowingHistory();
    }

    private void loadBorrowingHistory() {
        User user = SessionManager.getLoggedInUser();
        if (user == null) {
            return;
        }

        List<Borrowing> borrowings = borrowingService.getBorrowingsByUserId(user.getId());
        tableModel.setRowCount(0);

        for (Borrowing borrowing : borrowings) {
            tableModel.addRow(new Object[]{
                    borrowing.getId(),
                    borrowing.getCopy().getBook().getTitle(),
                    borrowing.getBorrowDate(),
                    borrowing.getReturnDate()
            });
        }
    }
}
