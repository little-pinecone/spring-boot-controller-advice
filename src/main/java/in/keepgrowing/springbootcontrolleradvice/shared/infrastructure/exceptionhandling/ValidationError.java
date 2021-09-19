package in.keepgrowing.springbootcontrolleradvice.shared.infrastructure.exceptionhandling;

import lombok.Data;

@Data
public class ValidationError {

    private final String fieldName;
    private final String brokenConstraint;
}
