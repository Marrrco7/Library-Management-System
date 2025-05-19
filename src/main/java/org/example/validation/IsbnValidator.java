package org.example.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validator class that implements the logic for validating ISBN formats.
 * It checks that the provided ISBN is not null or empty, removes any hyphens,
 * and then check that the characters left are either 10 or 13.
 *
 * <p>This validator is used with the {@code @ValidIsbn} annotation</p>
 */


public class IsbnValidator implements ConstraintValidator<ValidIsbn, String> {

    @Override
    public void initialize(ValidIsbn constraintAnnotation) {
    }

    @Override
    public boolean isValid(String isbn, ConstraintValidatorContext context) {
        if (isbn == null || isbn.isEmpty()) {
            return false;
        }


        String digitsOnly = isbn.replaceAll("-", "");
        if (digitsOnly.length() == 10 || digitsOnly.length() == 13) {
            return digitsOnly.matches("\\d+");
        }
        return false;
    }
}
