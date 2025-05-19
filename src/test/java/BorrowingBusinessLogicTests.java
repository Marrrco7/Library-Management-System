

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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
        userService.createUser("MaxBorrowUser", "maxborrow@example.com", "555-0000", "123 St", "pwd", "USER");
        User user = userService.getAllUsers().get(0);

        bookService.createBook("Book A", "Author A", "Pub A", 2022, "9781111111111");
        bookService.createBook("Book B", "Author B", "Pub B", 2023, "9782222222222");
        bookService.createBook("Book C", "Author C", "Pub C", 2024, "9783333333333");

        List<Book> books = bookService.getAllBooks();
        for (Book b : books) {
            copyService.createCopy(b.getId(), 1, "Available");
        }
        List<Copy> copies = copyService.getAllCopies();

        borrowingService.createBorrowing(user.getId(), copies.get(0).getId(), new Date(), null);
        borrowingService.createBorrowing(user.getId(), copies.get(1).getId(), new Date(), null);

        boolean borrowFailed = false;
        try {
            borrowingService.createBorrowing(user.getId(), copies.get(2).getId(), new Date(), null);
        } catch (IllegalStateException e) {
            borrowFailed = true;
        }
        assertTrue(borrowFailed, "3rd borrow should fail if max is 2 simultaneous borrowings");

        List<Borrowing> userBorrowings = borrowingService.getBorrowingsByUserId(user.getId());
        assertEquals(2, userBorrowings.size());
    }


    @Test
    void testBorrowingDateLogic() {
        userService.createUser("DateUser", "date@example.com", "555-1111", "101 St", "pwd", "USER");
        User user = userService.getAllUsers().get(0);

        bookService.createBook("Date Book", "Date Author", "Date Pub", 2022, "9784444444444");
        Book book = bookService.getAllBooks().get(0);

        copyService.createCopy(book.getId(), 1, "Available");
        Copy copy = copyService.getAllCopies().get(0);

        Date borrowDate = new Date();
        borrowingService.createBorrowing(user.getId(), copy.getId(), borrowDate, null);

        Borrowing b = borrowingService.getAllBorrowings().get(0);

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        assertEquals(df.format(borrowDate), df.format(b.getBorrowDate()), "Borrow date should match (date portion) of the provided date");
        assertNull(b.getReturnDate(), "Return date should be null initially if not specified");

        Date newReturnDate = new Date();
        borrowingService.updateBorrowing(b.getId(), b.getBorrowDate(), newReturnDate);

        Borrowing updated = borrowingService.getBorrowingById(b.getId());
        assertEquals(df.format(newReturnDate), df.format(updated.getReturnDate()), "Return date should be updated (date portion)");
    }

    @Test
    void testLibrarianOnlyPerformCertainActions() {
        // 1) Create a normal user and a librarian
        userService.createUser("RegularUser", "user@example.com", "555-2222", "222 St", "pwd", "USER");
        userService.createUser("LibUser", "lib@example.com", "555-3333", "333 St", "pwd", "LIBRARIAN");

        User normalUser = userService.getUserByEmail("user@example.com");
        User librarian = userService.getUserByEmail("lib@example.com");

        boolean userFailed = false;
        try {

            bookService.createBook(
                    normalUser.getRole(),
                    "NotAllowed Book",
                    "AuthorX",
                    "PubX",
                    2021,
                    "9785555555555"
            );
            fail("Expected an exception because normal user shouldn't create a book");
        } catch (Exception e) {
            userFailed = true;
        }
        assertTrue(userFailed, "Regular user should not be able to create a book if only librarians can do it");

        boolean librarianFailed = false;
        try {
            bookService.createBook(
                    librarian.getRole(),
                    "Allowed Book",
                    "AuthorY",
                    "PubY",
                    2022,
                    "9786666666666"
            );
        } catch (Exception e) {
            librarianFailed = true;
        }
        assertFalse(librarianFailed, "Librarian should be able to create a book without exception");

        List<Book> allBooks = bookService.getAllBooks();
        assertEquals(1, allBooks.size());
    }


}

