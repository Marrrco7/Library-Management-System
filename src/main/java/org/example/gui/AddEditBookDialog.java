package org.example.gui;

import org.example.model.Book;
import org.example.service.BookService;
import org.example.service.CopyService;

import javax.swing.*;
import java.awt.*;


/**
 * A dialog for adding or editing a book record in the library system.
 *
 * <p>This dialog provides fields for entering a book's title, author, publisher,
 * publication year, ISBN, and number of copies. Depending on whether a new
 * book is being created or an existing one is being edited, it interacts with
 * {@link BookService} to persist the book and with {@link CopyService} to create
 * copies when adding a new book.</p>
 *
 * <p>The dialog just uses a grid layout and swing components such as text fields
 * and buttons to collect user input and perform actions such as  saving or canceling.</p>
 */
public class AddEditBookDialog extends JDialog {

    private JTextField titleField, authorField, publisherField, yearField, isbnField, copiesField;
    private BookService bookService;
    private CopyService copyService;
    private int bookId = -1;

    public AddEditBookDialog(Frame parent, BookService bookService, CopyService copyService) {
        super(parent, "Add/Edit Book", true);
        this.bookService = bookService;
        this.copyService = copyService;

        setLayout(new GridLayout(7, 2));

        add(new JLabel("Title:"));
        titleField = new JTextField();
        add(titleField);

        add(new JLabel("Author:"));
        authorField = new JTextField();
        add(authorField);

        add(new JLabel("Publisher:"));
        publisherField = new JTextField();
        add(publisherField);

        add(new JLabel("Year:"));
        yearField = new JTextField();
        add(yearField);

        add(new JLabel("ISBN:"));
        isbnField = new JTextField();
        add(isbnField);

        add(new JLabel("Number of Copies:"));
        copiesField = new JTextField("1");
        add(copiesField);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> saveBook());
        add(saveButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        add(cancelButton);

        pack();
        setLocationRelativeTo(parent);
    }

    public void setBook(Book book) {
        this.bookId = book.getId();
        titleField.setText(book.getTitle());
        authorField.setText(book.getAuthor());
        publisherField.setText(book.getPublisher());
        yearField.setText(String.valueOf(book.getPublicationYear()));
        isbnField.setText(book.getIsbn());
        copiesField.setEnabled(false);
    }

    private void saveBook() {
        String title = titleField.getText();
        String author = authorField.getText();
        String publisher = publisherField.getText();
        int year = Integer.parseInt(yearField.getText());
        String isbn = isbnField.getText();
        int copies = Integer.parseInt(copiesField.getText());

        if (title.isEmpty() || author.isEmpty() || isbn.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Title, Author, and ISBN are required.");
            return;
        }

        if (bookId == -1) {
            bookService.createBook(title, author, publisher, year, isbn);
            int createdBookId = bookService.getBookByIsbn(isbn).getId();
            for (int i = 1; i <= copies; i++) {
                copyService.createCopy(createdBookId, i, "Available");
            }
            JOptionPane.showMessageDialog(this, "Book and copies added successfully.");
        } else {
            bookService.updateBook(bookId, title, author, publisher, year, isbn);
            JOptionPane.showMessageDialog(this, "Book updated successfully.");
        }
        dispose();
    }
}
