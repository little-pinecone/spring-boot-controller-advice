package in.keepgrowing.springbootcontrolleradvice.shared.infrastructure.exceptionhandling;

import in.keepgrowing.springbootcontrolleradvice.shared.infrastructure.validation.exceptions.InternalConstraintValidationException;
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

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestControllerAdvice
@Slf4j
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String INVALID_REQUEST_MESSAGE = "Invalid request.";
    private static final String TYPE_MISMATCH_MESSAGE_FORMAT = INVALID_REQUEST_MESSAGE +
            " Invalid value for '%s' (expected %s).";

    private final HttpMessageNotReadableDetailsProvider messageNotReadableDetailsProvider;
    private final SimpleTypeMapper simpleTypeMapper;

    public CustomExceptionHandler(HttpMessageNotReadableDetailsProvider messageNotReadableDetailsProvider,
                                  SimpleTypeMapper simpleTypeMapper) {
        this.messageNotReadableDetailsProvider = messageNotReadableDetailsProvider;
        this.simpleTypeMapper = simpleTypeMapper;
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
        String expectedType = simpleTypeMapper.map(ex.getRequiredType());
        String message = String.format(TYPE_MISMATCH_MESSAGE_FORMAT, ex.getName(), expectedType);
        var body = ErrorResponseBody.builder()
                .exceptionCode(ExceptionCode.CLIENT_ERROR)
                .message(message)
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

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleException(ConstraintViolationException ex, WebRequest request) {
        log.error("Request constraints were violated:", ex);
        var body = ErrorResponseBody.builder()
                .exceptionCode(ExceptionCode.CLIENT_ERROR)
                .message(INVALID_REQUEST_MESSAGE + " Check 'validationErrors' for details.")
                .validationErrors(getValidationErrors(ex.getConstraintViolations()))
                .build();

        return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    private ArrayList<ValidationError> getValidationErrors(Set<ConstraintViolation<?>> violations) {
        var validationErrors = new ArrayList<ValidationError>();
        violations.forEach(v -> {
            String lastNode = getLastNode(v.getPropertyPath());
            validationErrors.add(new ValidationError(lastNode, v.getMessage()));
        });

        return validationErrors;
    }

    private String getLastNode(Path violationPath) {
        var nodes = new ArrayList<String>();
        violationPath.forEach(node -> nodes.add(node.getName()));

        return nodes.get(nodes.size() - 1);
    }

    @ExceptionHandler(InternalConstraintValidationException.class)
    public ResponseEntity<Object> handleException(InternalConstraintValidationException ex, WebRequest request) {
        List<ValidationError> validationErrors = getValidationErrors(ex.getConstraintViolations());
        String validatedObject = ex.getValidatedObjectClass().getName();
        log.error("Constraints for " + validatedObject + " were violated internally:\n" + validationErrors, ex);
        var body = ErrorResponseBody.builder()
                .exceptionCode(ExceptionCode.INTERNAL_SERVER_ERROR)
                .build();

        return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}
