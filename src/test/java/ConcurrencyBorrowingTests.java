
import org.example.model.Book;
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
public class ConcurrencyBorrowingTests {

    private EntityManagerFactory emf;
    private BookService bookService;
    private CopyService copyService;
    private UserService userService;
    private BorrowingService borrowingService;

    @BeforeAll
    void setUpAll() {
        emf = Persistence.createEntityManagerFactory("testPU");

        bookService = new BookService("testPU");
        copyService = new CopyService("testPU");
        userService = new UserService("testPU");
        borrowingService = new BorrowingService("testPU");
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

    @Test
    void testConcurrentBorrowingOnSameCopy() throws InterruptedException {
        bookService.createBook("Concurrency Book", "Some Author", "Publisher", 2023, "9781234567897");
        Book book = bookService.getAllBooks().get(0);

        copyService.createCopy(book.getId(), 1, "Available");
        Copy copy = copyService.getAllCopies().get(0);

        userService.createUser("ConcurrentUser", "concurrent@example.com", "555-0000", "Concurrent St", "pass123", "USER");
        User user = userService.getAllUsers().get(0);

        Runnable borrowTask = () -> {
            try {
                borrowingService.createBorrowing(user.getId(), copy.getId(), new Date(), null);
            } catch (Exception e) {

            }
        };


        Thread t1 = new Thread(borrowTask, "BorrowThread-1");
        Thread t2 = new Thread(borrowTask, "BorrowThread-2");

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        List<?> allBorrowings = borrowingService.getAllBorrowings();
        assertEquals(1, allBorrowings.size(),
                "Only one Borrowing should exist if concurrency is handled properly");

        Copy updatedCopy = copyService.getAllCopies().get(0);
        assertEquals("Borrowed", updatedCopy.getStatus(),
                "Copy should be 'Borrowed' after one successful borrowing attempt");
    }
}
