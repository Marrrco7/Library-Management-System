
import org.example.model.*;
import org.example.service.*;
import org.junit.jupiter.api.*;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RelationshipTests {

    private UserService userService;
    private PublisherService publisherService;
    private BookService bookService;
    private CopyService copyService;
    private BorrowingService borrowingService;
    private LibrarianService librarianService;

    @BeforeAll
    void setup() {
        userService = new UserService("testPU");
        publisherService = new PublisherService("testPU");
        bookService = new BookService("testPU");
        copyService = new CopyService("testPU");
        borrowingService = new BorrowingService("testPU");
        librarianService = new LibrarianService("testPU");
    }

    @BeforeEach
    void cleanDatabase() {
        EntityManager em = Persistence.createEntityManagerFactory("testPU").createEntityManager();
        em.getTransaction().begin();
        em.createQuery("DELETE FROM Borrowing").executeUpdate();
        em.createQuery("DELETE FROM Librarian").executeUpdate();
        em.createQuery("DELETE FROM Copy").executeUpdate();
        em.createQuery("DELETE FROM Book").executeUpdate();
        em.createQuery("DELETE FROM Publisher").executeUpdate();
        em.createQuery("DELETE FROM User").executeUpdate();
        em.getTransaction().commit();
        em.close();
    }

    @Test
    void testUserBorrowingsRelationship() {
        userService.createUser("John Doe", "john@example.com", "123456789", "123 Elm St", "password", "USER");
        User user = userService.getAllUsers().get(0);

        publisherService.createPublisher("Penguin Books", "123 Publisher Ave", "555-1234");
        Publisher publisher = publisherService.getAllPublishers().get(0);

        bookService.createBook("Test Book", "Author A", publisher.getName(), 2025, "1234567898");
        Book book = bookService.getAllBooks().get(0);

        copyService.createCopy(book.getId(), 1, "Available");
        Copy copy = copyService.getAllCopies().get(0);

        borrowingService.createBorrowing(user.getId(), copy.getId(), new Date(), null);

        List<Borrowing> borrowings = borrowingService.getBorrowingsByUserId(user.getId());
        assertEquals(1, borrowings.size());
        assertEquals(user.getId(), borrowings.get(0).getUser().getId());
    }

    @Test
    void testBookCopiesRelationship() {
        publisherService.createPublisher("HarperCollins", "456 Publisher Rd", "555-6789");
        Publisher publisher = publisherService.getAllPublishers().get(0);

        bookService.createBook("Test Book 2", "Author B", publisher.getName(), 2023, "2134567898");
        Book book = bookService.getAllBooks().get(0);

        copyService.createCopy(book.getId(), 1, "Available");
        copyService.createCopy(book.getId(), 2, "Available");

        List<Copy> copies = copyService.getAllCopies();
        assertEquals(2, copies.size());
        for (Copy c : copies) {
            assertEquals(book.getId(), c.getBook().getId());
        }
    }

    @Test
    void testUserLibrarianRelationship() {
        userService.createUser("Jane Doe", "jane@example.com", "987654321", "789 Maple St", "password", "LIBRARIAN");
        User user = userService.getAllUsers().get(0);

        librarianService.createLibrarian(user.getId(), "Head Librarian", new Date());
        Librarian librarian = librarianService.getAllLibrarians().get(0);

        assertNotNull(librarian);
        assertEquals(user.getId(), librarian.getUser().getId());
    }

    @Test
    void testBookPublisherRelationship() {
        publisherService.createPublisher("Publisher A", "123 Publisher Lane", "555-0001");
        publisherService.createPublisher("Publisher B", "456 Publisher St", "555-0002");

        Publisher publisherA = publisherService.getAllPublishers().get(0);
        Publisher publisherB = publisherService.getAllPublishers().get(1);

        bookService.createBook("Book A", "Author A", publisherA.getName(), 2020, "1234567898");
        Book book = bookService.getAllBooks().get(0);

        assertEquals(publisherA.getName(), book.getPublisher());

        bookService.updateBook(book.getId(), "Book A Updated", "Author A", publisherB.getName(), 2021, "1234567890");

        Book updatedBook = bookService.getBookById(book.getId());
        assertEquals(publisherB.getName(), updatedBook.getPublisher());
    }
}
