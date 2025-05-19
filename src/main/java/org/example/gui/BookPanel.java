package org.example.gui;

import org.example.model.Book;
import org.example.service.BookService;
import org.example.service.CopyService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;


/**
 * A panel that displays a list of books and provides options to add, edit, or delete books.
 *
 * <p>This panel uses a JTable to show book details like ID, title, author, publisher, year,
 * and isbn. It interacts with {@link BookService} to retrieve and manipulate book data
 * from the database, and uses {@link CopyService} when adding a new book to handle
 * the creation of copies.</p>
 *
 * <p>The panel provides buttons for adding, editing, and deleting books, and updates
 * the table view accordingly when changes are made.</p>
 */
public class BookPanel extends JPanel {

    private BookService bookService;
    private JTable bookTable;
    private DefaultTableModel tableModel;

    public BookPanel() {
        bookService = new BookService();
        setLayout(new BorderLayout());


        String[] columnNames = {"ID", "Title", "Author", "Publisher", "Year", "ISBN"};
        tableModel = new DefaultTableModel(columnNames, 0);
        bookTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(bookTable);
        add(scrollPane, BorderLayout.CENTER);


        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Book");
        JButton editButton = new JButton("Edit Book");
        JButton deleteButton = new JButton("Delete Book");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);


        loadBooks();

        addButton.addActionListener(e -> openAddDialog());
        editButton.addActionListener(e -> openEditDialog());
        deleteButton.addActionListener(e -> deleteBook());
    }

    private void loadBooks() {
        tableModel.setRowCount(0);
        List<Book> books = bookService.getAllBooks();
        for (Book book : books) {
            tableModel.addRow(new Object[]{
                    book.getId(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getPublisher(),
                    book.getPublicationYear(),
                    book.getIsbn()
            });
        }
    }

    private void openAddDialog() {
        AddEditBookDialog dialog = new AddEditBookDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                bookService,
                new CopyService()
        );
        dialog.setVisible(true);
        loadBooks();
    }

    private void openEditDialog() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book to edit.");
            return;
        }

        int bookId = (int) tableModel.getValueAt(selectedRow, 0);
        Book book = bookService.getBookById(bookId);

        AddEditBookDialog dialog = new AddEditBookDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                bookService,
                new CopyService()
        );
        dialog.setBook(book);
        dialog.setVisible(true);
        loadBooks();
    }

    private void deleteBook() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book to delete.");
            return;
        }

        int bookId = (int) tableModel.getValueAt(selectedRow, 0);
        int confirmation = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this book?",
                "Delete Book",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmation == JOptionPane.YES_OPTION) {
            bookService.deleteBook(bookId);
            loadBooks();
        }
    }
}
