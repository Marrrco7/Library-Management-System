
import org.example.model.Book;
import org.example.model.Borrowing;
import org.example.model.Copy;
import org.example.model.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import javax.persistence.*;
import javax.validation.ConstraintViolationException;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class NullOrEmptyFieldsTests {

    private EntityManagerFactory emf;

    @BeforeAll
    void setup() {
        emf = Persistence.createEntityManagerFactory("testPU");
    }

    @AfterAll
    void tearDown() {
        if (emf.isOpen()) {
            emf.close();
        }
    }

    @Test
    void testNullEmailShouldFail() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        User user = new User("John Doe", null, "555-1234", "123 Main St", "password123", "USER");

        assertThrows(ConstraintViolationException.class, () -> {
            em.persist(user);
            em.getTransaction().commit();
        });

        em.close();
    }

    @Test
    void testNullPasswordShouldFail() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        User user = new User("Jane Doe", "jane@example.com", "555-5678", "456 Elm St", null, "USER");

        assertThrows(PersistenceException.class, () -> {
            em.persist(user);
            em.getTransaction().commit();
        });

        em.close();
    }

    @Test
    void testBorrowingWithNullBorrowDateFails() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        User user = new User("Bob", "bob@example.com", "555-9876", "999 Maple", "pwd", "USER");
        em.persist(user);

        Book book = new Book("Title", "Author", "Publisher", 2023, "1234567890");
        em.persist(book);

        Copy copy = new Copy(book, 1, "Available");
        em.persist(copy);

        Borrowing borrowing = new Borrowing(user, copy, null, null);

        assertThrows(PersistenceException.class, () -> {
            em.persist(borrowing);
            em.getTransaction().commit();
        });

        em.close();
    }

    @Test
    void testBorrowingWithNullReturnDateSucceeds() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        User user = new User("Jane Doe", "jane@example.com", "555-6789", "456 Maple St", "securePass123", "USER");
        em.persist(user);

        Book book = new Book("Test Book", "Author A", "Publisher X", 2025, "978-3-16-148410-0"); // Valid ISBN
        em.persist(book);

        Copy copy = new Copy(book, 1, "Available");
        em.persist(copy);

        Borrowing borrowing = new Borrowing(user, copy, new Date(), null); // Null returnDate
        em.persist(borrowing);
        em.getTransaction().commit();

        Borrowing retrievedBorrowing = em.find(Borrowing.class, borrowing.getId());
        assertNotNull(retrievedBorrowing);
        assertNull(retrievedBorrowing.getReturnDate());
        assertEquals(user.getId(), retrievedBorrowing.getUser().getId());
        assertEquals(copy.getId(), retrievedBorrowing.getCopy().getId());

        em.close();
    }


}
