package org.example.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


/**
 * Validator class that implements the logic for validating the email addresses.
 * It checks that the email is not null, not empty, and matches a pattern from the typical format for emails.
 *
 * <p>This class is used along with the {@link ValidEmail} annotation
 * to enforce email format constraints on the email field.</p>
 */

public class EmailValidator implements ConstraintValidator<ValidEmail, String> {

    @Override
    public void initialize(ValidEmail constraintAnnotation) {
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        return email.matches("^[^@]+@[^@]+\\.[^@]+$");
    }
}
