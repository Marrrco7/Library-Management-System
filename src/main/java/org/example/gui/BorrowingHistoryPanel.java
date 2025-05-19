package org.example.gui;

import org.example.service.BorrowingService;

import javax.swing.*;
import java.awt.*;

public class BorrowingHistoryPanel extends JPanel {

    private BorrowingService borrowingService;

    public BorrowingHistoryPanel() {
        borrowingService = new BorrowingService();
        setLayout(new BorderLayout());

        // Table to display borrowing history
        String[] columnNames = {"ID", "Title", "Borrow Date", "Return Date"};
        JTable historyTable = new JTable(new Object[0][4], columnNames);
        JScrollPane scrollPane = new JScrollPane(historyTable);

        add(scrollPane, BorderLayout.CENTER);
    }
}
