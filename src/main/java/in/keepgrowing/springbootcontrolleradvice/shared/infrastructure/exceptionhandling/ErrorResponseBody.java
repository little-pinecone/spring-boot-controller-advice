package in.keepgrowing.springbootcontrolleradvice.shared.infrastructure.exceptionhandling;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@EqualsAndHashCode
@ToString
public final class ErrorResponseBody {

    private static final String DEFAULT_MESSAGE = "The request could not be processed.";

    private final ExceptionCode exceptionCode;
    private final List<ValidationError> validationErrors;
    private final String message;

    @Builder
    public ErrorResponseBody(ExceptionCode exceptionCode, String message, List<ValidationError> validationErrors) {
        this.exceptionCode = exceptionCode == null
                ? ExceptionCode.INTERNAL_SERVER_ERROR
                : exceptionCode;
        this.message = message == null
                ? DEFAULT_MESSAGE
                : message;
        this.validationErrors = validationErrors == null
                ? new ArrayList<>()
                : validationErrors;
    }
}
