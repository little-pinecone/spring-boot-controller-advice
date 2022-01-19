package in.keepgrowing.springbootcontrolleradvice.shared.infrastructure.validation.exceptions;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

public class InternalConstraintValidationException extends ConstraintViolationException {

    public InternalConstraintValidationException(String message,
                                                 Set<? extends ConstraintViolation<?>> constraintViolations) {
        super(message, constraintViolations);
    }
}
