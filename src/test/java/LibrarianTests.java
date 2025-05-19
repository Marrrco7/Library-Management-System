
import org.example.model.Librarian;
import org.example.model.User;
import org.example.service.LibrarianService;
import org.example.service.UserService;
import org.junit.jupiter.api.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LibrarianTests {

    private EntityManagerFactory emf;
    private LibrarianService librarianService;
    private UserService userService;

    @BeforeAll
    void setUpAll() {
        emf = Persistence.createEntityManagerFactory("testPU");

        librarianService = new LibrarianService("testPU");
        userService = new UserService("testPU");
    }

    @BeforeEach
    void cleanDatabase() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

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

    private int createTestUser() {
        userService.createUser(
                "Test User",
                "test@example.com",
                "555-1234",
                "123 Main St",
                "password123",
                "USER_ROLE"
        );
        List<User> users = userService.getAllUsers();
        return users.get(users.size() - 1).getId();
    }

    @Test
    void testCreateLibrarian() {
        int userId = createTestUser();

        Date employmentDate = new Date();
        librarianService.createLibrarian(userId, "Head Librarian", employmentDate);

        List<Librarian> allLibrarians = librarianService.getAllLibrarians();
        assertEquals(1, allLibrarians.size());

        Librarian librarian = allLibrarians.get(0);
        assertNotNull(librarian.getId(), "Librarian ID should be set.");

        assertEquals("Head Librarian", librarian.getPosition());
        assertEquals(userId, librarian.getUser().getId());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        assertEquals(df.format(employmentDate), df.format(librarian.getEmploymentDate()));
    }

    @Test
    void testReadLibrarianById() {
        int userId = createTestUser();

        Date employmentDate = new Date();
        librarianService.createLibrarian(userId, "Assistant Librarian", employmentDate);

        Librarian created = librarianService.getAllLibrarians().get(0);
        int librarianId = created.getId();

        Librarian retrieved = librarianService.getLibrarianById(librarianId);
        assertNotNull(retrieved, "Librarian should be found by ID");

        assertEquals("Assistant Librarian", retrieved.getPosition());
        assertEquals(userId, retrieved.getUser().getId());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        assertEquals(df.format(employmentDate), df.format(retrieved.getEmploymentDate()));
    }

    @Test
    void testUpdateLibrarian() {
        int userId = createTestUser();

        Date employmentDate = new Date();
        librarianService.createLibrarian(userId, "Junior Librarian", employmentDate);

        Librarian librarian = librarianService.getAllLibrarians().get(0);
        int librarianId = librarian.getId();

        Date newDate = new Date();
        librarianService.updateLibrarian(librarianId, "Senior Librarian", newDate);

        Librarian updated = librarianService.getLibrarianById(librarianId);
        assertEquals("Senior Librarian", updated.getPosition());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        assertEquals(df.format(newDate), df.format(updated.getEmploymentDate()));
    }

    @Test
    void testDeleteLibrarian() {
        int userId = createTestUser();

        librarianService.createLibrarian(userId, "Temporary Librarian", new Date());

        Librarian librarian = librarianService.getAllLibrarians().get(0);
        int librarianId = librarian.getId();

        librarianService.deleteLibrarian(librarianId);

        assertNull(librarianService.getLibrarianById(librarianId),
                "Librarian should be removed from the database");
    }

    @Test
    void testDeleteLibrarian_SucceedsWhenNoReferences() {
        int userId = createTestUser();
        librarianService.createLibrarian(userId, "Temporary Librarian", new Date());

        Librarian librarian = librarianService.getAllLibrarians().get(0);
        int librarianId = librarian.getId();

        librarianService.deleteLibrarian(librarianId);

        assertNull(librarianService.getLibrarianById(librarianId));
    }

}
