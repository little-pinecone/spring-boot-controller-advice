package in.keepgrowing.springbootcontrolleradvice.shared.infrastructure.validation.exceptions;

import lombok.Getter;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

public class InternalConstraintValidationException extends ConstraintViolationException {

    @Getter
    private final Class<?> validatedObjectClass;

    public InternalConstraintValidationException(String message,
                                                 Set<? extends ConstraintViolation<?>> constraintViolations,
                                                 Class<?> validatedObjectClass) {
        super(message, constraintViolations);
        this.validatedObjectClass = validatedObjectClass;
    }
}
