package org.example.validation;


/**
 * Custom annotation to validate that a given string conforms to the ISBN format.
 * It uses {@link IsbnValidator} to perform the actual validation.
 *
 * <p>This annotation is applied for the ISBN field in book entity {@link IsbnValidator}.</p>
 *
 * <p>Standard attributes include:</p>
 */
import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = IsbnValidator.class)
@Documented
public @interface ValidIsbn {
    String message() default "Invalid ISBN format";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
