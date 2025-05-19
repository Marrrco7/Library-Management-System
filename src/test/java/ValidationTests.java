package org.example.tests;

import org.example.model.Book;
import org.example.model.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.validation.*;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ValidationTests {

    private static Validator validator;

    /**
     * Build the Validator instance once before all tests.
     * This loads the Bean Validation framework (e.g. Hibernate Validator).
     */
    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // ------------------------
    // Testing Book.isbn
    // ------------------------

    @Test
    @DisplayName("Valid ISBN passes validation")
    void testValidIsbn() {
        // Suppose your ISBN validation requires a 10 or 13 digit string:
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
        // e.g., too short or contains letters in places
        Book invalidBook = new Book(
                "Title",
                "Author",
                "Publisher",
                2022,
                "ABCD-1234" // obviously invalid
        );
        Set<ConstraintViolation<Book>> violations = validator.validate(invalidBook);
        assertFalse(violations.isEmpty(), "Expected violation for an invalid ISBN format");

        // Optionally, you can check the exact message or property path:
        ConstraintViolation<Book> violation = violations.iterator().next();
        assertEquals("isbn", violation.getPropertyPath().toString(),
                "Violation should be on the 'isbn' field");
        // or check the message if your custom annotation has a default message
        // assertEquals("Invalid ISBN format", violation.getMessage());
    }

    @Test
    @DisplayName("Null or empty ISBN fails validation")
    void testNullIsbn() {
        Book nullIsbnBook = new Book(
                "No ISBN Title",
                "Author",
                "Publisher",
                2022,
                null // null
        );
        Set<ConstraintViolation<Book>> violations = validator.validate(nullIsbnBook);
        assertFalse(violations.isEmpty(), "Should fail because ISBN is null");
    }

    // ------------------------
    // Testing User.email
    // ------------------------

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

        // Further checks if desired:
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("email", violation.getPropertyPath().toString());
        // assertEquals("Invalid email format", violation.getMessage());
    }

    @Test
    @DisplayName("Null email fails validation")
    void testNullEmail() {
        User user = new User(
                "Charlie",
                null, // null email
                "555-9999",
                "789 Boulevard",
                "pwd",
                "ADMIN"
        );
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Expected violation for null email");
    }

}
