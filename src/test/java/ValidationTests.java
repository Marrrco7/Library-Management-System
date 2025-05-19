
import org.example.model.Book;
import org.example.model.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.validation.*;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ValidationTests {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Valid ISBN passes validation")
    void testValidIsbn() {
        Book validBook = new Book(
                "Valid Title",
                "Some Author",
                "Some Publisher",
                2022,
                "1234567890" // 10-digit ISBN
        );
        Set<ConstraintViolation<Book>> violations = validator.validate(validBook);
        assertTrue(violations.isEmpty(), "Expected no violations for a valid ISBN");
    }

    @Test
    @DisplayName("Invalid ISBN fails validation")
    void testInvalidIsbn() {
        Book invalidBook = new Book(
                "Title",
                "Author",
                "Publisher",
                2022,
                "ABCD-1234"
        );
        Set<ConstraintViolation<Book>> violations = validator.validate(invalidBook);
        assertFalse(violations.isEmpty(), "Expected violation for an invalid ISBN format");

        ConstraintViolation<Book> violation = violations.iterator().next();
        assertEquals("isbn", violation.getPropertyPath().toString(),
                "Violation should be on the 'isbn' field");
    }

    @Test
    @DisplayName("Null or empty ISBN fails validation")
    void testNullIsbn() {
        Book nullIsbnBook = new Book(
                "No ISBN Title",
                "Author",
                "Publisher",
                2022,
                null
        );
        Set<ConstraintViolation<Book>> violations = validator.validate(nullIsbnBook);
        assertFalse(violations.isEmpty(), "Should fail because ISBN is null");
    }


    @Test
    @DisplayName("Valid email passes validation")
    void testValidEmail() {
        User validUser = new User(
                "Alice",
                "alice@example.com",
                "555-1234",
                "123 Street",
                "password123",
                "USER"
        );
        Set<ConstraintViolation<User>> violations = validator.validate(validUser);
        assertTrue(violations.isEmpty(), "Expected no violations for a valid email");
    }

    @Test
    @DisplayName("Invalid email fails validation")
    void testInvalidEmail() {
        User invalidUser = new User(
                "Bob",
                "not-an-email",
                "555-5678",
                "456 Avenue",
                "password456",
                "USER"
        );
        Set<ConstraintViolation<User>> violations = validator.validate(invalidUser);
        assertFalse(violations.isEmpty(), "Expected violation for invalid email format");

        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("email", violation.getPropertyPath().toString());
    }

    @Test
    @DisplayName("Null email fails validation")
    void testNullEmail() {
        User user = new User(
                "Charlie",
                null,
                "555-9999",
                "789 Boulevard",
                "pwd",
                "ADMIN"
        );
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Expected violation for null email");
    }


}
