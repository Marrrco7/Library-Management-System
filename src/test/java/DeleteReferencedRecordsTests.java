
import org.example.service.UserService;
import org.example.service.BookService;
import org.example.service.CopyService;
import org.example.service.BorrowingService;
import org.example.model.Book;
import org.example.model.Copy;
import org.example.model.User;
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
    void testDeleteUserWithActiveBorrowingsFails() {
        // 1) Create user
        userService.createUser("Alice", "alice@example.com", "555-1234", "123 Main St", "password", "USER");
        User user = userService.getAllUsers().get(0);

        // 2) Create Book + Copy
        bookService.createBook("A Book", "An Author", "A Publisher", 2023, "9781111111111");
        Book book = bookService.getAllBooks().get(0);

        copyService.createCopy(book.getId(), 1, "Available");
        Copy copy = copyService.getAllCopies().get(0);

        // 3) Create Borrowing referencing the user
        borrowingService.createBorrowing(user.getId(), copy.getId(), new Date(), null);

        // 4) Attempt to delete user - expect an exception or failure if your code is set to disallow
        boolean deleteFailed = false;
        try {
            userService.deleteUser(user.getId());
        } catch (Exception e) {
            deleteFailed = true;
        }

        // 5) If your code logic or DB constraints forbid deleting a user with borrowings, test passes
        assertTrue(deleteFailed, "Deleting a user with active borrowings should fail.");

        // Optional: confirm user still exists
        User stillThere = userService.getUserById(user.getId());
        assertNotNull(stillThere);
    }
}
