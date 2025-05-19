package org.example.gui;

import org.example.model.Publisher;
import org.example.service.PublisherService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel for managing publisher records.
 *
 * <p>This JPanel displays a table of publishers including their ID, name, address,
 * and phone number. It provides buttons to add, edit, and delete publishers,
 * interacting with {@link PublisherService} to perform the operations.</p>
 *
 * <p>Users can interact with the panel to manage publisher data, and the table
 * refreshes to reflect the current state of the database after any changes.</p>
 */

public class PublisherPanel extends JPanel {

    private JTable publisherTable;
    private DefaultTableModel tableModel;
    private PublisherService publisherService;

    public PublisherPanel() {
        publisherService = new PublisherService();
        setLayout(new BorderLayout());


        String[] columnNames = {"ID", "Name", "Address", "Phone Number"};
        tableModel = new DefaultTableModel(columnNames, 0);
        publisherTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(publisherTable);
        add(scrollPane, BorderLayout.CENTER);


        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Publisher");
        JButton editButton = new JButton("Edit Publisher");
        JButton deleteButton = new JButton("Delete Publisher");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);


        addButton.addActionListener(e -> showAddPublisherDialog());
        editButton.addActionListener(e -> showEditPublisherDialog());
        deleteButton.addActionListener(e -> deleteSelectedPublisher());

        loadData();
    }

    private void loadData() {
        List<Publisher> publishers = publisherService.getAllPublishers();
        tableModel.setRowCount(0);

        for (Publisher publisher : publishers) {
            tableModel.addRow(new Object[]{
                    publisher.getId(),
                    publisher.getName(),
                    publisher.getAddress(),
                    publisher.getPhoneNumber()
            });
        }
    }

    private void showAddPublisherDialog() {
        AddEditPublisherDialog dialog = new AddEditPublisherDialog(
                (Frame) SwingUtilities.getWindowAncestor(this), publisherService);
        dialog.setVisible(true);
        loadData();
    }

    private void showEditPublisherDialog() {
        int selectedRow = publisherTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a publisher to edit.");
            return;
        }


        int publisherId = (int) tableModel.getValueAt(selectedRow, 0);
        Publisher publisher = publisherService.getPublisherById(publisherId);

        AddEditPublisherDialog dialog = new AddEditPublisherDialog(
                (Frame) SwingUtilities.getWindowAncestor(this), publisherService);
        dialog.setPublisher(
                publisher.getId(),
                publisher.getName(),
                publisher.getAddress(),
                publisher.getPhoneNumber()
        );
        dialog.setVisible(true);

        loadData();
    }

    private void deleteSelectedPublisher() {
        int selectedRow = publisherTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a publisher to delete.");
            return;
        }

        int publisherId = (int) tableModel.getValueAt(selectedRow, 0);
        int confirmation = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this publisher?",
                "Delete Publisher",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmation == JOptionPane.YES_OPTION) {
            publisherService.deletePublisher(publisherId);
            loadData();
            JOptionPane.showMessageDialog(this, "Publisher deleted successfully.");
        }
    }
}
