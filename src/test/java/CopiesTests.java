

import org.example.model.Copy;
import org.example.model.Book;
import org.example.service.CopyService;
import org.example.service.BookService;
import org.example.service.BorrowingService;
import org.example.model.Borrowing;  // if needed for a "fails if referencing" test

import org.junit.jupiter.api.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import java.util.List;
import java.util.Date;  // Only if you do a referencing test with Borrowing's date
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CopiesTests {

    private EntityManagerFactory emf;
    private CopyService copyService;
    private BookService bookService;
    private BorrowingService borrowingService; // only if you want to test referencing

    @BeforeAll
    void setUpAll() {
        // Use the "testPU" so you don't affect production data
        emf = Persistence.createEntityManagerFactory("testPU");

        // If your CopyService has a constructor that takes a PU name:
        copyService = new CopyService("testPU");
        bookService = new BookService("testPU");

        // If you want to test referencing from Borrowing:
        borrowingService = new BorrowingService("testPU");
    }

    @BeforeEach
    void cleanDatabase() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        // Clear out tables in an order that respects FK constraints
        // Adjust these names to match your actual entity classes
        em.createQuery("DELETE FROM Borrowing").executeUpdate(); // if referencing Borrowing
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

    /**
     * Creates a single Book record and returns its primary key (ID).
     * Adjust to match your actual BookService method signature.
     */
    private int createTestBook() {
        // Example: public void createBook(String title, String author,
        //                                String publisher, int publicationYear, String isbn)
        bookService.createBook("Effective Java", "Joshua Bloch", "Addison-Wesley", 2018, "9780134685991");

        // Retrieve ID from the newly persisted Book
        List<Book> books = bookService.getAllBooks();
        return books.get(books.size() - 1).getId();
    }

    @Test
    void testCreateCopy() {
        // 1) Create a Book
        int bookId = createTestBook();

        // 2) Create a Copy referencing that Book
        copyService.createCopy(bookId, 1, "Available");

        // 3) Verify the Copy was inserted
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

        // Grab the newly created Copy
        Copy created = copyService.getAllCopies().get(0);
        int copyId = created.getId();

        // Now read it back by ID
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

        // Update the copy number and status
        copyService.updateCopy(copyId, 5, "Borrowed");

        // Retrieve updated copy
        Copy updated = copyService.getCopyById(copyId);
        assertEquals(5, updated.getCopyNumber(), "Copy number should be updated to 5");
        assertEquals("Borrowed", updated.getStatus(), "Status should be updated to 'Borrowed'");
    }

    @Test
    void testDeleteCopy_SucceedsWhenNoReferences() {
        int bookId = createTestBook();
        copyService.createCopy(bookId, 1, "Available");

        // Grab the newly created copy
        Copy copy = copyService.getAllCopies().get(0);
        int copyId = copy.getId();

        // Delete it
        copyService.deleteCopy(copyId);

        // Verify it no longer exists
        assertNull(copyService.getCopyById(copyId), "Copy should be deleted from the DB.");
    }

    /**
     * This test only applies if some entity references Copy (e.g. Borrowing).
     * If your DB is configured to disallow deleting a Copy that is referenced,
     * we expect an exception or failure.
     * If not, this test will fail because the DB will let you delete it.
     */
    @Test
    void testDeleteCopy_FailsIfReferencedByBorrowing() {
        // 1) Create Book
        int bookId = createTestBook();

        // 2) Create Copy
        copyService.createCopy(bookId, 10, "Available");
        Copy copy = copyService.getAllCopies().get(0);

        // 3) Create Borrowing referencing this Copy
        // We'll need a user, but let's do minimal approach:
        // If your BorrowingService requires a user, you'd create a test user similarly
        // or skip if your Borrowing doesn't require user.
        // Example:
        // userService.createUser("Alice", "alice@example.com", "555-9999", "Street 1", "pwd", "USER");
        // int userId = userService.getAllUsers().get(0).getId();

        // For demonstration, assume userId=1 or create a user. Then:
        // borrowingService.createBorrowing(userId, copy.getId(), new Date(), null);

        // 4) Try to delete the Copy
        boolean deleteFailed = false;
        try {
            copyService.deleteCopy(copy.getId());
        } catch (Exception e) {
            deleteFailed = true; // Expect a constraint violation if ON DELETE RESTRICT
        }

        assertTrue(deleteFailed,
                "Deleting a Copy that is referenced by Borrowing should fail if there's a FK constraint on Copy.");
    }

}
