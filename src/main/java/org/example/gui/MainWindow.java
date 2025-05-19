package org.example.gui;

import org.example.model.User;
import org.example.session.SessionManager;

import javax.swing.*;
import java.awt.*;



/**
 * The main window of the Library Management System.
 *
 * <p>This JFrame works as the primary window for the application. It sets up
 * the menu bar with Manage and View menus, controls navigation between
 * different modules (such as managing users, books, borrowings, publishers,
 * and viewing titles or borrowing history), and adjusts available options
 * based on the logged in user's role.</p>
 *
 * <p>It uses {@link SessionManager} in order to determine if the current user is a librarian or not,
 * and enables or disables management features accordingly. The main window
 * dynamically loads different panels into its  area based on the user interactions
 * with the menu.</p>
 */
public class MainWindow extends JFrame {

    User loggedInUser = SessionManager.getLoggedInUser();
    boolean isLibrarian = "LIBRARIAN".equals(loggedInUser.getRole());

    public MainWindow() {
        setTitle("Library Management System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());


        JMenuBar menuBar = new JMenuBar();

        JMenu manageMenu = new JMenu("Manage");
        JMenuItem manageUsers = new JMenuItem("Users");
        JMenuItem manageBooks = new JMenuItem("Books");
        JMenuItem manageBorrowings = new JMenuItem("Borrowings");
        JMenuItem managePublishers = new JMenuItem("Publishers");
        manageMenu.add(manageUsers);
        manageMenu.add(manageBooks);
        manageMenu.add(manageBorrowings);
        manageMenu.add(managePublishers);

        if (!isLibrarian) {
            manageMenu.setEnabled(false);
        }

        JMenu viewMenu = new JMenu("View");
        JMenuItem viewAllTitles = new JMenuItem("All Titles");
        JMenuItem viewAvailableTitles = new JMenuItem("Available Titles");
        JMenuItem viewBorrowingHistory = new JMenuItem("My Borrowing History");
        viewMenu.add(viewAllTitles);
        viewMenu.add(viewAvailableTitles);
        viewMenu.add(viewBorrowingHistory);

        menuBar.add(manageMenu);
        menuBar.add(viewMenu);

        setJMenuBar(menuBar);

        JMenuItem manageLibrarians = new JMenuItem("Librarians");
        manageMenu.add(manageLibrarians);


        JPanel mainPanel = new JPanel();
        mainPanel.add(new JLabel("Welcome to the Library Management System"));
        add(mainPanel, BorderLayout.CENTER);


        manageUsers.addActionListener(e -> showPanel(new UserPanel()));
        manageBooks.addActionListener(e -> showPanel(new BookPanel()));
        manageBorrowings.addActionListener(e -> showPanel(new BorrowingPanel()));
        viewAllTitles.addActionListener(e -> showPanel(new ViewAllTitlesPanel()));
        viewAvailableTitles.addActionListener(e -> showPanel(new ViewAvailableTitlesPanel()));
        manageLibrarians.addActionListener(e -> showPanel(new LibrarianPanel()));
        managePublishers.addActionListener(e -> showPanel(new PublisherPanel()));
        viewBorrowingHistory.addActionListener(e -> {
            if (!SessionManager.isLoggedIn()) {
                JOptionPane.showMessageDialog(this, "Please log in first.");
                return;
            }
            showPanel(new BorrowingHistoryPanel());
        });

    }




    private void showPanel(JPanel panel) {
        getContentPane().removeAll();
        add(panel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainWindow mainWindow = new MainWindow();
            mainWindow.setVisible(true);
        });
    }


}
