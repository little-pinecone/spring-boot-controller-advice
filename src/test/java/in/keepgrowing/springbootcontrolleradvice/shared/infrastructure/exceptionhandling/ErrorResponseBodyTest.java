package in.keepgrowing.springbootcontrolleradvice.shared.infrastructure.exceptionhandling;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseBodyTest {

    @Test
    void shouldBuildWithDefaultValues() {
        var actual = ErrorResponseBody.builder()
                .build();

        assertNotNull(actual);

        assertAll(
                () -> assertEquals(ExceptionCode.INTERNAL_SERVER_ERROR, actual.getExceptionCode()),
                () -> assertFalse(actual.getMessage().isBlank()),
                () -> assertNotNull(actual.getValidationErrors()),
                () -> assertTrue(actual.getValidationErrors().isEmpty())
        );
    }

    @Test
    void shouldBuildWithGivenValues() {
        var validationError = new ValidationError("field", "constraint");
        var actual = ErrorResponseBody.builder()
                .exceptionCode(ExceptionCode.CLIENT_ERROR)
                .message("Test message")
                .validationErrors(List.of(validationError))
                .build();

        assertNotNull(actual);

        assertAll(
                () -> assertEquals(ExceptionCode.CLIENT_ERROR, actual.getExceptionCode()),
                () -> assertEquals("Test message", actual.getMessage()),
                () -> assertEquals("field", actual.getValidationErrors().get(0).getFieldName())
        );
    }
}