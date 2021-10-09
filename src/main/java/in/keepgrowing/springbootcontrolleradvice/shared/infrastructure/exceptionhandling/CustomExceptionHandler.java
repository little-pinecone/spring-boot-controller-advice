package in.keepgrowing.springbootcontrolleradvice.shared.infrastructure.exceptionhandling;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    public static final String INVALID_REQUEST_MESSAGE = "Invalid request.";

    private final HttpMessageNotReadableDetailsProvider messageNotReadableDetailsProvider;

    public CustomExceptionHandler(HttpMessageNotReadableDetailsProvider messageNotReadableDetailsProvider) {
        this.messageNotReadableDetailsProvider = messageNotReadableDetailsProvider;
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleException(RuntimeException ex, WebRequest request) {
        log.error("Unhandled exception has occurred:", ex);
        var body = ErrorResponseBody.builder()
                .exceptionCode(ExceptionCode.INTERNAL_SERVER_ERROR)
                .build();

        return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleException(MethodArgumentTypeMismatchException ex, WebRequest request) {
        log.error("Invalid argument type:", ex);
        var body = ErrorResponseBody.builder()
                .exceptionCode(ExceptionCode.CLIENT_ERROR)
                .message(INVALID_REQUEST_MESSAGE + " Verify the type of provided arguments.")
                .build();

        return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {
        log.error("Invalid argument:", ex);
        var body = ErrorResponseBody.builder()
                .exceptionCode(ExceptionCode.CLIENT_ERROR)
                .message(INVALID_REQUEST_MESSAGE + " Check 'validationErrors' for details.")
                .validationErrors(getValidationErrors(ex.getFieldErrors()))
                .build();

        return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.UNPROCESSABLE_ENTITY, request);
    }

    private ArrayList<ValidationError> getValidationErrors(List<FieldError> errors) {
        var validationErrors = new ArrayList<ValidationError>();
        errors.forEach(e -> {
            var validationError = new ValidationError(e.getField(), e.getDefaultMessage());
            validationErrors.add(validationError);
        });

        return validationErrors;
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {
        log.error("Message not readable:", ex);
        String errorDetails = messageNotReadableDetailsProvider.getDetails(ex);
        var body = ErrorResponseBody.builder()
                .exceptionCode(ExceptionCode.CLIENT_ERROR)
                .message(INVALID_REQUEST_MESSAGE + " " + errorDetails)
                .build();

        return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }
}
