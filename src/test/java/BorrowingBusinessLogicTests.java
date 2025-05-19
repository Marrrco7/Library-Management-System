

import org.example.model.Book;
import org.example.model.Borrowing;
import org.example.model.Copy;
import org.example.model.User;
import org.example.service.BookService;
import org.example.service.BorrowingService;
import org.example.service.CopyService;
import org.example.service.UserService;
import org.junit.jupiter.api.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BorrowingBusinessLogicTests {

    private EntityManagerFactory emf;
    private UserService userService;
    private BookService bookService;
    private CopyService copyService;
    private BorrowingService borrowingService;

    @BeforeAll
    void setupAll() {
        emf = Persistence.createEntityManagerFactory("testPU");
        userService = new UserService("testPU");
        bookService = new BookService("testPU");
        copyService = new CopyService("testPU");
        borrowingService = new BorrowingService("testPU");
    }

    @BeforeEach
    void clearDatabase() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.createQuery("DELETE FROM Borrowing").executeUpdate();
        em.createQuery("DELETE FROM Copy").executeUpdate();
        em.createQuery("DELETE FROM Book").executeUpdate();
        em.createQuery("DELETE FROM User").executeUpdate();
        em.getTransaction().commit();
        em.close();
    }

    @AfterAll
    void tearDownAll() {
        if (emf.isOpen()) {
            emf.close();
        }
    }

    @Test
    void testMaxSimultaneousBorrowings() {
        // Suppose your logic says: a user can only have 2 borrowings at once.
        // We'll see if the 3rd attempt fails.
        userService.createUser("MaxBorrowUser", "maxborrow@example.com", "555-0000", "123 St", "pwd", "USER");
        User user = userService.getAllUsers().get(0);

        // Create a couple of Books + Copies
        bookService.createBook("Book A", "Author A", "Pub A", 2022, "9781111111111");
        bookService.createBook("Book B", "Author B", "Pub B", 2023, "9782222222222");
        bookService.createBook("Book C", "Author C", "Pub C", 2024, "9783333333333");

        List<Book> books = bookService.getAllBooks();
        // Create copies for each
        for (Book b : books) {
            copyService.createCopy(b.getId(), 1, "Available");
        }
        List<Copy> copies = copyService.getAllCopies();

        // Borrow first 2 copies -> should succeed
        borrowingService.createBorrowing(user.getId(), copies.get(0).getId(), new Date(), null);
        borrowingService.createBorrowing(user.getId(), copies.get(1).getId(), new Date(), null);

        // Attempt 3rd borrowing -> if your logic says max = 2, it should fail
        boolean borrowFailed = false;
        try {
            borrowingService.createBorrowing(user.getId(), copies.get(2).getId(), new Date(), null);
        } catch (Exception e) {
            borrowFailed = true;
        }
        assertTrue(borrowFailed, "3rd borrow should fail if max is 2 simultaneous borrowings");

        // Confirm the user only has 2 borrowings in the DB
        List<Borrowing> userBorrowings = borrowingService.getBorrowingsByUserId(user.getId());
        assertEquals(2, userBorrowings.size());
    }

    @Test
    void testBorrowingDateLogic() {
        // Checks that borrowDate and returnDate are set or handled properly
        userService.createUser("DateUser", "date@example.com", "555-1111", "101 St", "pwd", "USER");
        User user = userService.getAllUsers().get(0);

        bookService.createBook("Date Book", "Date Author", "Date Pub", 2022, "9784444444444");
        Book book = bookService.getAllBooks().get(0);

        copyService.createCopy(book.getId(), 1, "Available");
        Copy copy = copyService.getAllCopies().get(0);

        Date borrowDate = new Date();
        borrowingService.createBorrowing(user.getId(), copy.getId(), borrowDate, null);

        // Check that the persisted borrowing has the correct borrowDate
        Borrowing b = borrowingService.getAllBorrowings().get(0);
        assertEquals(borrowDate, b.getBorrowDate(), "Borrow date should match the provided date");
        assertNull(b.getReturnDate(), "Return date should be null initially if not specified");

        // Suppose we later set a return date
        Date returnDate = new Date();
        borrowingService.updateBorrowing(b.getId(), borrowDate, returnDate);
        Borrowing updated = borrowingService.getBorrowingById(b.getId());
        assertEquals(returnDate, updated.getReturnDate(), "Return date should be updated properly");
    }

    @Test
    void testLibrarianOnlyPerformCertainActions() {
        // For example, only a librarian can create a new Book or something similar
        // We simulate a user trying to do it vs. a librarian trying
        userService.createUser("RegularUser", "user@example.com", "555-2222", "222 St", "pwd", "USER");
        userService.createUser("LibUser", "lib@example.com", "555-3333", "333 St", "pwd", "LIBRARIAN");

        User normalUser = userService.getUserByEmail("user@example.com");
        User librarian = userService.getUserByEmail("lib@example.com");

        // Let's suppose we have a method requiring a role check, e.g. bookService.createBookIfLibrarian
        // If your code doesn't do it yet, this test won't pass without changes.
        // We'll show a pseudo approach:

        boolean userFailed = false;
        try {
            // Attempt to create a book using a normal user -> should fail or throw exception
            createBookIfLibrarian(normalUser, "NotAllowed Book", "AuthorX", "PubX", 2021, "9785555555555");
        } catch (Exception e) {
            userFailed = true;
        }
        assertTrue(userFailed, "Regular user should not be able to create a book if only librarians can do it");

        // Now the librarian tries -> should succeed
        try {
            createBookIfLibrarian(librarian, "Allowed Book", "AuthorY", "PubY", 2022, "9786666666666");
        } catch (Exception e) {
            fail("Librarian should be able to create a book but got exception: " + e.getMessage());
        }

        // Confirm that we have 1 book in DB
        List<Book> allBooks = bookService.getAllBooks();
        assertEquals(1, allBooks.size());
    }

    // Example "pseudo" method that checks a user's role before calling the real BookService
    // This is not an existing method, just a demonstration of how you'd do role checks.
    private void createBookIfLibrarian(User user, String title, String author, String publisher, int year, String isbn) {
        if (!"LIBRARIAN".equalsIgnoreCase(user.getRole())) {
            throw new IllegalStateException("Only librarians can create books!");
        }
        bookService.createBook(title, author, publisher, year, isbn);
    }
}

