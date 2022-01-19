package in.keepgrowing.springbootcontrolleradvice.shared.domain.validation;

/**
 * Validates given objects and throws ConstraintValidationException
 */
public interface ThrowingValidator {

    void validate(Object object);
}
