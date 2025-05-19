package org.example.gui;

import org.example.service.CopyService;

import javax.swing.*;
import java.awt.*;

public class ViewAvailableTitlesPanel extends JPanel {

    private CopyService copyService;

    public ViewAvailableTitlesPanel() {
        copyService = new CopyService();
        setLayout(new BorderLayout());

        // Table to display available titles
        String[] columnNames = {"ID", "Title", "Author", "Copy Number", "Status"};
        JTable availableTitlesTable = new JTable(new Object[0][5], columnNames);
        JScrollPane scrollPane = new JScrollPane(availableTitlesTable);

        add(scrollPane, BorderLayout.CENTER);
    }
}
