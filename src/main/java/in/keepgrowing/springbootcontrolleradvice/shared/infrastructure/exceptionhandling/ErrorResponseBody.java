package in.keepgrowing.springbootcontrolleradvice.shared.infrastructure.exceptionhandling;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public class ErrorResponseBody {

    public static final String DEFAULT_MESSAGE = "The request could not be processed";

    private final ExceptionCode exceptionCode;
    private String message;

    private ErrorResponseBody(ExceptionCode exceptionCode) {
        this.exceptionCode = exceptionCode;
    }

    public static ErrorResponseBody of(ExceptionCode exceptionCode) {
        var responseBody = new ErrorResponseBody(exceptionCode);
        responseBody.message = DEFAULT_MESSAGE;

        return responseBody;
    }
}
