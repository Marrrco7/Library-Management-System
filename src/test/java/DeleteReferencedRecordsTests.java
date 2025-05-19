
import org.example.model.Book;
import org.example.model.Borrowing;
import org.example.model.Copy;
import org.example.model.User;
import org.example.model.Librarian;
import org.example.service.BookService;
import org.example.service.BorrowingService;
import org.example.service.CopyService;
import org.example.service.UserService;
import org.example.service.LibrarianService;
import org.junit.jupiter.api.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DeleteReferencedRecordsTests {

    private EntityManagerFactory emf;
    private UserService userService;
    private BookService bookService;
    private CopyService copyService;
    private BorrowingService borrowingService;
    private LibrarianService librarianService;

    @BeforeAll
    void setupAll() {
        emf = Persistence.createEntityManagerFactory("testPU");
        userService = new UserService("testPU");
        bookService = new BookService("testPU");
        copyService = new CopyService("testPU");
        borrowingService = new BorrowingService("testPU");
        librarianService = new LibrarianService("testPU");
    }

    @BeforeEach
    void clearDatabase() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.createQuery("DELETE FROM Borrowing").executeUpdate();
        em.createQuery("DELETE FROM Copy").executeUpdate();
        em.createQuery("DELETE FROM Book").executeUpdate();
        em.createQuery("DELETE FROM Librarian").executeUpdate();
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
    void testDeleteUserWithActiveBorrowingsFails() {
        userService.createUser("Alice", "alice@example.com", "555-1234", "123 Main St", "password", "USER");
        User user = userService.getAllUsers().get(0);

        bookService.createBook("A Book", "An Author", "A Publisher", 2023, "9781111111111");
        Book book = bookService.getAllBooks().get(0);

        copyService.createCopy(book.getId(), 1, "Available");
        Copy copy = copyService.getAllCopies().get(0);

        borrowingService.createBorrowing(user.getId(), copy.getId(), new Date(), null);

        boolean deleteFailed = false;
        try {
            userService.deleteUser(user.getId());
        } catch (Exception e) {
            deleteFailed = true;
        }
        assertTrue(deleteFailed);
        User stillThere = userService.getUserById(user.getId());
        assertNotNull(stillThere);
    }

    @Test
    void testDeleteBookWithActiveBorrowingFails() {
        userService.createUser("Bob", "bob@example.com", "555-5678", "456 Elm St", "pass", "USER");
        User user = userService.getAllUsers().get(0);

        bookService.createBook("Active Borrow Book", "Author B", "Pub B", 2023, "9782222222222");
        Book book = bookService.getAllBooks().get(0);

        copyService.createCopy(book.getId(), 1, "Available");
        Copy copy = copyService.getAllCopies().get(0);

        borrowingService.createBorrowing(user.getId(), copy.getId(), new Date(), null);

        boolean deleteFailed = false;
        try {
            bookService.deleteBook(book.getId());
        } catch (Exception e) {
            deleteFailed = true;
        }
        assertTrue(deleteFailed);

        Book stillExists = bookService.getBookById(book.getId());
        assertNotNull(stillExists);
    }

    @Test
    void testDeleteUserWhoIsAlsoLibrarianFails() {
        userService.createUser("Charles", "charles@example.com", "555-9999", "789 Maple", "pwd", "USER");
        User user = userService.getAllUsers().get(0);

        librarianService.createLibrarian(user.getId(), "Head Librarian", new Date());
        Librarian librarian = librarianService.getAllLibrarians().get(0);

        boolean deleteFailed = false;
        try {
            userService.deleteUser(user.getId());
        } catch (Exception e) {
            deleteFailed = true;
        }
        assertTrue(deleteFailed);

        User stillThere = userService.getUserById(user.getId());
        assertNotNull(stillThere);
        Librarian stillLib = librarianService.getLibrarianById(librarian.getId());
        assertNotNull(stillLib);
    }

    @Test
    void testDeleteBookReferencedByCopiesFails() {
        bookService.createBook("Reference Book", "Ref Author", "Ref Pub", 2023, "9783333333333");
        Book book = bookService.getAllBooks().get(0);

        copyService.createCopy(book.getId(), 1, "Available");
        Copy copy = copyService.getAllCopies().get(0);

        boolean deleteFailed = false;
        try {
            bookService.deleteBook(book.getId());
        } catch (Exception e) {
            deleteFailed = true;
        }
        assertTrue(deleteFailed);

        Book stillBook = bookService.getBookById(book.getId());
        assertNotNull(stillBook);
    }
}
