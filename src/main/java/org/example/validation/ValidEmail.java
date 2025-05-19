package org.example.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Custom annotation for validating that a given string is a valid email address.
 * It uses the {@link EmailValidator} class to do the actual validation logic.
 *
 * <p>Annotation for the email field in the user entity. If the value is null or does not match
 * the expected pattern, a validation error will occur.</p>
 */


@Target({ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EmailValidator.class)
@Documented
public @interface ValidEmail {
    String message() default "Invalid email format";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
