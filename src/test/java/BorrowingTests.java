

import org.example.model.Borrowing;
import org.example.model.User;
import org.example.model.Copy;
import org.example.service.BorrowingService;
import org.example.service.UserService;
import org.example.service.BookService;
import org.example.service.CopyService;
import org.junit.jupiter.api.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.text.SimpleDateFormat;  // For date comparison
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BorrowingTests {

    private EntityManagerFactory emf;
    private BorrowingService borrowingService;
    private UserService userService;
    private BookService bookService;
    private CopyService copyService;

    @BeforeAll
    void setUpAll() {
        emf = Persistence.createEntityManagerFactory("testPU");
        borrowingService = new BorrowingService("testPU");
        userService = new UserService("testPU");
        bookService = new BookService("testPU");
        copyService = new CopyService("testPU");
    }

    @BeforeEach
    void cleanDatabase() {
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

    private int createTestUser() {
        userService.createUser("John Doe", "john@example.com", "555-1234", "123 Main St", "secret123", "USER_ROLE");
        List<User> allUsers = userService.getAllUsers();
        return allUsers.get(allUsers.size() - 1).getId();
    }

    private int createTestBook() {
        bookService.createBook("Sample Title", "Sample Author", "Sample Publisher", 2021, "1234567892");
        return bookService.getAllBooks().get(0).getId();
    }

    private int createTestCopy(int bookId) {
        copyService.createCopy(bookId, 1, "Available");
        List<Copy> allCopies = copyService.getAllCopies();
        return allCopies.get(allCopies.size() - 1).getId();
    }

    @Test
    void testCreateBorrowing() {
        int userId = createTestUser();
        int bookId = createTestBook();
        int copyId = createTestCopy(bookId);

        Date borrowDate = new Date();
        borrowingService.createBorrowing(userId, copyId, borrowDate, null);

        List<Borrowing> allBorrowings = borrowingService.getAllBorrowings();
        assertEquals(1, allBorrowings.size());
        Borrowing b = allBorrowings.get(0);
        assertNotNull(b.getId());
        assertEquals(userId, b.getUser().getId());
        assertEquals(copyId, b.getCopy().getId());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        assertEquals(df.format(borrowDate), df.format(b.getBorrowDate()));

        assertNull(b.getReturnDate());
    }

    @Test
    void testReadBorrowingById() {
        int userId = createTestUser();
        int bookId = createTestBook();
        int copyId = createTestCopy(bookId);

        Date borrowDate = new Date();
        borrowingService.createBorrowing(userId, copyId, borrowDate, null);

        Borrowing created = borrowingService.getAllBorrowings().get(0);
        Borrowing retrieved = borrowingService.getBorrowingById(created.getId());

        assertNotNull(retrieved);

        assertEquals(userId, retrieved.getUser().getId());
        assertEquals(copyId, retrieved.getCopy().getId());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        assertEquals(df.format(borrowDate), df.format(retrieved.getBorrowDate()));
    }

    @Test
    void testUpdateBorrowing() {
        int userId = createTestUser();
        int bookId = createTestBook();
        int copyId = createTestCopy(bookId);

        Date borrowDate = new Date();
        borrowingService.createBorrowing(userId, copyId, borrowDate, null);

        Borrowing b = borrowingService.getAllBorrowings().get(0);
        Date returnDate = new Date();
        borrowingService.updateBorrowing(b.getId(), borrowDate, returnDate);

        Borrowing updated = borrowingService.getBorrowingById(b.getId());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        assertEquals(df.format(returnDate), df.format(updated.getReturnDate()));
    }

    @Test
    void testDeleteBorrowing_SucceedsWhenNoReferences() {
        int userId = createTestUser();
        int bookId = createTestBook();
        int copyId = createTestCopy(bookId);

        borrowingService.createBorrowing(userId, copyId, new Date(), null);

        Borrowing b = borrowingService.getAllBorrowings().get(0);
        borrowingService.deleteBorrowing(b.getId());

        assertNull(borrowingService.getBorrowingById(b.getId()));
    }

}
