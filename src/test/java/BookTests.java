
import org.example.model.Book;
import org.example.model.Copy;
import org.example.service.BookService;
import org.example.service.CopyService;
import org.junit.jupiter.api.*;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BookTests {

    private BookService bookService;
    private CopyService copyService;

    @BeforeAll
    void setup() {
        bookService = new BookService("testPU");
        copyService = new CopyService("testPU");
    }

    @BeforeEach
    void cleanDatabase() {
        EntityManager em = Persistence.createEntityManagerFactory("testPU").createEntityManager();
        em.getTransaction().begin();

        em.createQuery("DELETE FROM Borrowing").executeUpdate();
        em.createQuery("DELETE FROM Copy").executeUpdate();
        em.createQuery("DELETE FROM Book").executeUpdate();
        em.createQuery("DELETE FROM User").executeUpdate();
        em.createQuery("DELETE FROM Librarian").executeUpdate();
        em.createQuery("DELETE FROM Publisher").executeUpdate();

        em.getTransaction().commit();
        em.close();
    }

    @Test
    void testCreateBook() {
        cleanDatabase();


        bookService.createBook("The Great Gatsby", "F. Scott Fitzgerald", "Charles Scribner's Sons", 1925, "9780743273565");


        List<Book> books = bookService.getAllBooks();
        assertFalse(books.isEmpty(), "Book list should not be empty after creation.");

        Book book = books.get(0);
        assertEquals("The Great Gatsby", book.getTitle());
        assertEquals("F. Scott Fitzgerald", book.getAuthor());
        assertEquals("Charles Scribner's Sons", book.getPublisher());
        assertEquals(1925, book.getPublicationYear());
        assertEquals("9780743273565", book.getIsbn());
    }

    @Test
    void testReadBookById() {
        bookService.createBook("1984", "George Orwell", "Secker & Warburg", 1949, "9780451524935");

        Book book = bookService.getAllBooks().get(0);
        Book retrievedBook = bookService.getBookById(book.getId());

        assertNotNull(retrievedBook, "Book should exist in the database.");
        assertEquals("1984", retrievedBook.getTitle());
        assertEquals("George Orwell", retrievedBook.getAuthor());
        assertEquals("Secker & Warburg", retrievedBook.getPublisher());
        assertEquals(1949, retrievedBook.getPublicationYear());
        assertEquals("9780451524935", retrievedBook.getIsbn());
    }

    @Test
    void testUpdateBook() {
        bookService.createBook("Animal Farm", "George Orwell", "Secker & Warburg", 1945, "9780451526342");
        Book book = bookService.getAllBooks().get(0);

        bookService.updateBook(book.getId(), "Animal Farm (Updated)", "George Orwell", "Penguin Classics", 1951, "9780141036137");

        Book updatedBook = bookService.getBookById(book.getId());

        assertEquals("Animal Farm (Updated)", updatedBook.getTitle());
        assertEquals("Penguin Classics", updatedBook.getPublisher());
        assertEquals(1951, updatedBook.getPublicationYear());
        assertEquals("9780141036137", updatedBook.getIsbn());
    }

    @Test
    void testDeleteBook() {
        bookService.createBook("Test Book", "Test Author", "Test Publisher", 2021, "9781234567890");
        Book book = bookService.getAllBooks().get(0);

        bookService.deleteBook(book.getId());

        assertNull(bookService.getBookById(book.getId()), "Book should be deleted.");
    }

    @Test
    void testDeleteBookReferencedByCopiesFails() {
        bookService.createBook("Referenced Book", "Some Author", "Some Publisher", 2000, "9789876543210");
        Book book = bookService.getAllBooks().get(0);

        copyService.createCopy(book.getId(), 1, "AVAILABLE");

        boolean deleteFailed = false;
        try {
            bookService.deleteBook(book.getId());
        } catch (Exception e) {
            deleteFailed = true;
        }

        assertTrue(deleteFailed);
    }



}

