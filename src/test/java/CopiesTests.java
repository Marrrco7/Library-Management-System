

import org.example.model.Copy;
import org.example.model.Book;
import org.example.service.CopyService;
import org.example.service.BookService;
import org.example.service.BorrowingService;
 // if needed for a "fails if referencing" test

import org.junit.jupiter.api.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import java.util.List;
 // Only if you do a referencing test with Borrowing's date
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CopiesTests {

    private EntityManagerFactory emf;
    private CopyService copyService;
    private BookService bookService;
    private BorrowingService borrowingService;

    @BeforeAll
    void setUpAll() {
        emf = Persistence.createEntityManagerFactory("testPU");

        copyService = new CopyService("testPU");
        bookService = new BookService("testPU");

        borrowingService = new BorrowingService("testPU");
    }

    @BeforeEach
    void cleanDatabase() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        em.createQuery("DELETE FROM Borrowing").executeUpdate();
        em.createQuery("DELETE FROM Copy").executeUpdate();
        em.createQuery("DELETE FROM Book").executeUpdate();

        em.getTransaction().commit();
        em.close();
    }

    @AfterAll
    void tearDownAll() {
        if (emf.isOpen()) {
            emf.close();
        }
    }

    private int createTestBook() {
        bookService.createBook("Effective Java", "Joshua Bloch", "Addison-Wesley", 2018, "9780134685991");

        List<Book> books = bookService.getAllBooks();
        return books.get(books.size() - 1).getId();
    }

    @Test
    void testCreateCopy() {
        int bookId = createTestBook();

        copyService.createCopy(bookId, 1, "Available");

        List<Copy> allCopies = copyService.getAllCopies();
        assertFalse(allCopies.isEmpty(), "Expected at least 1 copy after creation.");

        Copy copy = allCopies.get(0);
        assertNotNull(copy.getId(), "Copy ID should be auto-generated.");
        assertEquals("Available", copy.getStatus());
        assertEquals(1, copy.getCopyNumber());
        assertEquals(bookId, copy.getBook().getId(), "Copy should reference the correct Book.");
    }

    @Test
    void testReadCopyById() {
        int bookId = createTestBook();
        copyService.createCopy(bookId, 2, "Available");

        Copy created = copyService.getAllCopies().get(0);
        int copyId = created.getId();

        Copy retrieved = copyService.getCopyById(copyId);
        assertNotNull(retrieved, "Copy should be found by ID");
        assertEquals(2, retrieved.getCopyNumber());
        assertEquals("Available", retrieved.getStatus());
        assertEquals(bookId, retrieved.getBook().getId());
    }

    @Test
    void testUpdateCopy() {
        int bookId = createTestBook();
        copyService.createCopy(bookId, 3, "Available");

        Copy created = copyService.getAllCopies().get(0);
        int copyId = created.getId();

        copyService.updateCopy(copyId, 5, "Borrowed");

        Copy updated = copyService.getCopyById(copyId);
        assertEquals(5, updated.getCopyNumber(), "Copy number should be updated to 5");
        assertEquals("Borrowed", updated.getStatus(), "Status should be updated to 'Borrowed'");
    }

    @Test
    void testDeleteCopy_SucceedsWhenNoReferences() {
        int bookId = createTestBook();
        copyService.createCopy(bookId, 1, "Available");

        Copy copy = copyService.getAllCopies().get(0);
        int copyId = copy.getId();

        copyService.deleteCopy(copyId);

        assertNull(copyService.getCopyById(copyId), "Copy should be deleted from the DB.");
    }



}
