package org.example.gui;

import org.example.model.Copy;
import org.example.service.CopyService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel for viewing available book copies in the library system.
 *
 * <p>This panel displays a table of available book copies, including details
 * such as ID, title, author, copy number, and status. It uses the {@link CopyService}
 * to retrieve all the copies that are currently available for borrowing, and then
 * populates a JTable with this data.</p>
 *
 * <p>Users can use this panel in order to see which titles are available in the library
 * along with their copy numbers and status.</p>
 */
public class ViewAvailableTitlesPanel extends JPanel {

    private CopyService copyService;

    public ViewAvailableTitlesPanel() {
        copyService = new CopyService();
        setLayout(new BorderLayout());

        String[] columnNames = {"ID", "Title", "Author", "Copy Number", "Status"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable availableTitlesTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(availableTitlesTable);


        populateTable(tableModel);

        add(scrollPane, BorderLayout.CENTER);
    }

    private void populateTable(DefaultTableModel tableModel) {
        List<Copy> copies = copyService.getAvailableCopies();
        for (Copy copy : copies) {
            Object[] rowData = {
                    copy.getId(),
                    copy.getBook().getTitle(),
                    copy.getBook().getAuthor(),
                    copy.getCopyNumber(),
                    copy.getStatus()
            };
            tableModel.addRow(rowData);
        }
    }
}
