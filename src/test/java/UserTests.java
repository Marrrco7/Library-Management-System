import org.example.model.User;
import org.example.service.UserService;
import org.junit.jupiter.api.*;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserTests {

    private UserService userService;

    @BeforeAll
    void setup() {
        userService = new UserService("testPU");
    }

    @BeforeEach
    void cleanDatabase() {
        EntityManager em = Persistence.createEntityManagerFactory("testPU").createEntityManager();
        em.getTransaction().begin();

        em.createQuery("DELETE FROM Borrowing").executeUpdate();
        em.createQuery("DELETE FROM Librarian").executeUpdate();
        em.createQuery("DELETE FROM Copy").executeUpdate();
        em.createQuery("DELETE FROM Publisher").executeUpdate();
        em.createQuery("DELETE FROM Book").executeUpdate();
        em.createQuery("DELETE FROM User").executeUpdate();

        em.getTransaction().commit();
        em.close();
    }

    @Test
    void testCreateUser() {
        userService.createUser("Alice", "alice@example.com", "123456789", "123 Main St", "password123", "USER");
        List<User> users = userService.getAllUsers();
        assertFalse(users.isEmpty());
        assertEquals("Alice", users.get(0).getName());
    }

    @Test
    void testReadUserById() {
        userService.createUser("Bob", "bob@example.com", "987654321", "456 Elm St", "password456", "USER");
        User user = userService.getAllUsers().get(0);
        User retrievedUser = userService.getUserById(user.getId());
        assertNotNull(retrievedUser);
        assertEquals("Bob", retrievedUser.getName());
    }

    @Test
    void testUpdateUser() {
        userService.createUser("Charlie", "charlie@example.com", "999888777", "789 Oak St", "password789", "USER");
        User user = userService.getAllUsers().get(0);
        userService.updateUser(user.getId(), "Charlie Updated", "charlie.updated@example.com", "111222333", "456 Pine St", "newpassword123", "LIBRARIAN");
        User updatedUser = userService.getUserById(user.getId());
        assertEquals("Charlie Updated", updatedUser.getName());
    }

    @Test
    void testDeleteUser() {
        userService.createUser("David", "david@example.com", "444555666", "321 Birch St", "password000", "USER");
        User user = userService.getAllUsers().get(0);
        userService.deleteUser(user.getId());
        assertNull(userService.getUserById(user.getId()));
    }
}
