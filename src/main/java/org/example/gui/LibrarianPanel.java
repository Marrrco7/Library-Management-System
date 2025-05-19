package org.example.gui;

import org.example.model.Librarian;
import org.example.model.User;
import org.example.service.LibrarianService;
import org.example.service.UserService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * For displaying and managing librarians.
 *
 * <p>This panel displays a table of librarians, including their Ids, associated user names,
 * positions, and employment dates. It provides buttons to add, edit, and delete librarians.
 * Interactions on this panel utilize {@link LibrarianService} and {@link UserService}
 * to perform CRUD operations on librarian records.</p>
 *
 * <p>Users can add a new librarian, edit the details of an existing one, or delete a librarian.
 * The table refreshes after each operation to display the current list of librarians.</p>
 */

public class LibrarianPanel extends JPanel {

    private LibrarianService librarianService;
    private JTable librarianTable;
    private UserService userService;
    private DefaultTableModel tableModel;

    public LibrarianPanel() {
        librarianService = new LibrarianService();
        userService = new UserService();
        setLayout(new BorderLayout());

        String[] columnNames = {"ID", "User Name", "Position", "Employment Date"};
        tableModel = new DefaultTableModel(columnNames, 0);
        librarianTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(librarianTable);
        add(scrollPane, BorderLayout.CENTER);


        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Librarian");
        buttonPanel.add(addButton);
        add(buttonPanel, BorderLayout.SOUTH);

        JButton deleteButton = new JButton("Delete Librarian");
        buttonPanel.add(deleteButton);

        JButton editButton = new JButton("Edit Librarian");
        buttonPanel.add(editButton);


        deleteButton.addActionListener(e -> deleteSelectedLibrarian());

        editButton.addActionListener(e -> showEditLibrarianDialog());


        addButton.addActionListener(e -> showAddLibrarianDialog());
        loadData();
    }

    private void loadData() {
        List<Librarian> librarians = librarianService.getAllLibrarians();
        tableModel.setRowCount(0);

        for (Librarian librarian : librarians) {
            tableModel.addRow(new Object[]{
                    librarian.getId(),
                    librarian.getUser().getName(),
                    librarian.getPosition(),
                    librarian.getEmploymentDate()
            });
        }
    }



    private void showAddLibrarianDialog() {
        AddEditLibrarianDialog dialog = new AddEditLibrarianDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                new LibrarianService(),
                new UserService()
        );
        dialog.setVisible(true);
        loadData();
    }

    private void deleteSelectedLibrarian() {
        int selectedRow = librarianTable.getSelectedRow();
        if (selectedRow != -1) {
            int librarianId = (int) tableModel.getValueAt(selectedRow, 0);
            int confirmation = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to delete this librarian?",
                    "Delete Librarian",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirmation == JOptionPane.YES_OPTION) {
                librarianService.deleteLibrarian(librarianId);
                loadData();
                JOptionPane.showMessageDialog(this, "Librarian deleted successfully.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a librarian to delete.");
        }
    }

    private void showEditLibrarianDialog() {
        int selectedRow = librarianTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a librarian to edit.");
            return;
        }


        int librarianId = (int) tableModel.getValueAt(selectedRow, 0);
        Librarian librarian = librarianService.getLibrarianById(librarianId);

        AddEditLibrarianDialog dialog = new AddEditLibrarianDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                librarianService,
                userService
        );
        dialog.setLibrarian(
                librarian.getId(),
                librarian.getUser().getId(),
                librarian.getPosition(),
                librarian.getEmploymentDate()
        );
        dialog.setVisible(true);


        loadData();
    }




}
