package in.keepgrowing.springbootcontrolleradvice.shared.infrastructure.validation.validators;

import in.keepgrowing.springbootcontrolleradvice.shared.domain.validation.ThrowingValidator;
import in.keepgrowing.springbootcontrolleradvice.shared.infrastructure.validation.exceptions.InternalConstraintValidationException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;

import javax.validation.ConstraintViolationException;
import java.util.Collection;
import java.util.Objects;

@Aspect
public class ControllerResponseValidator {

    private final ThrowingValidator validator;

    public ControllerResponseValidator(ThrowingValidator validator) {
        this.validator = validator;
    }

    @AfterReturning(
            pointcut = "@within(org.springframework.web.bind.annotation.RestController))",
            returning = "response")
    public void validateResponseBody(JoinPoint joinPoint, ResponseEntity<?> response) {
        if (response.getBody() instanceof Collection<?> res) {
            Objects.requireNonNull(res)
                    .forEach(this::validateObject);
        } else {
            validateObject(response.getBody());
        }
    }

    private void validateObject(Object response) {
        try {
            validator.validate(response);
        } catch (ConstraintViolationException e) {
            throw new InternalConstraintValidationException(e.getMessage(), e.getConstraintViolations());
        }
    }
}
