package org.example.gui;

import org.example.model.Book;
import org.example.service.BookService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;


/**
 * Panel for viewing all book titles in the library system.
 *
 * <p>This panel displays a table of all books available in the library,
 * including their ID, title, author, publisher, publication year, and Isbn.</p>
 *
 * <p>It retrieves the list of all books from {@link BookService} and populates
 * a JTable with this data, allowing the users to easily view all the titles.</p>
 */

public class ViewAllTitlesPanel extends JPanel {

    private BookService bookService;

    public ViewAllTitlesPanel() {
        bookService = new BookService();
        setLayout(new BorderLayout());

        String[] columnNames = {"ID", "Title", "Author", "Publisher", "Year", "ISBN"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable allTitlesTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(allTitlesTable);


        populateTable(tableModel);

        add(scrollPane, BorderLayout.CENTER);
    }

    private void populateTable(DefaultTableModel tableModel) {
        List<Book> books = bookService.getAllBooks();
        for (Book book : books) {
            Object[] rowData = {
                    book.getId(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getPublisher(),
                    book.getPublicationYear(),
                    book.getIsbn()
            };
            tableModel.addRow(rowData);
        }
    }
}
