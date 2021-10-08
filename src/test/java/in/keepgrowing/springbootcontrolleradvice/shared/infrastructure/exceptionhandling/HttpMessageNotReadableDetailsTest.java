package in.keepgrowing.springbootcontrolleradvice.shared.infrastructure.exceptionhandling;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HttpMessageNotReadableDetailsTest {

    private static final List<Class<?>> TYPES = List.of(byte.class, short.class, int.class, long.class, char.class,
            boolean.class, Integer.class, Long.class, Double.class, Float.class, Instant.class, ZonedDateTime.class,
            LocalDateTime.class, LocalDate.class, String.class, Character.class, UUID.class, Boolean.class, List.class);
    private static final Pattern UNKNOWN_TYPE = Pattern.compile(HttpMessageNotReadableDetails.UNKNOWN_TYPE);

    private HttpMessageNotReadableDetails messageNotReadableDetails;

    @BeforeEach
    void setUp() {
        messageNotReadableDetails = new HttpMessageNotReadableDetails();
    }

    @Test
    void shouldProvideDetailsForMismatchedInputExceptionWithPaths() {
        var exception = getMismatchInputExceptionWithPaths();
        var expectedMessage = "Invalid value for field1[3].field2 (expected a floating point number) " +
                "at line: 20, column: 30 in json.";

        var actual = messageNotReadableDetails.getDetails(exception);

        assertEquals(expectedMessage, actual);
    }

    private MismatchedInputException getMismatchInputExceptionWithPaths() {
        var exception = getMismatchedInputException(Double.class);

        exception.prependPath(new JsonMappingException.Reference(null, "field2"));
        exception.prependPath(new JsonMappingException.Reference(null, 3));
        exception.prependPath(new JsonMappingException.Reference(null, "field1"));

        return exception;
    }

    private MismatchedInputException getMismatchedInputException(Class<?> requiredType) {
        var parser = getJsonParser();

        return MismatchedInputException.from(parser, requiredType, "irrelevant");
    }

    private JsonParser getJsonParser() {
        var parser = mock(JsonParser.class);

        when(parser.getTokenLocation())
                .thenReturn(new JsonLocation("sourceRef", 10, 20, 30));

        return parser;
    }

    @Test
    void shouldProvideDetailsForMismatchedInputExceptionWhenRequiredTypeIsUnknown() {
        var exception = getMismatchedInputException(Map.class);

        var actual = messageNotReadableDetails.getDetails(exception);
        var bicMatcher = UNKNOWN_TYPE.matcher(actual);

        assertTrue(bicMatcher.find());
    }

    @Test
    void shouldProvideDetailsForMismatchedInputExceptionForSupportedTypes() {
        TYPES.forEach(type -> {
            var exception = getMismatchedInputException(type);

            var actual = messageNotReadableDetails.getDetails(exception);
            var bicMatcher = UNKNOWN_TYPE.matcher(actual);

            assertFalse(bicMatcher.find(), "Unhandled type: " + type.getName());
        });
    }

    @Test
    void shouldProvideDetailsForMismatchedInputExceptionForEnums() {
        var exception = getMismatchedInputException(TestEnum.class);
        var expectedMessage = "Invalid value for the request (expected an enum [A, B]) at line: 20, column: 30 in json.";

        var actual = messageNotReadableDetails.getDetails(exception);

        assertEquals(expectedMessage, actual);
    }

    @Test
    void shouldProvideDetailsForJsonProcessingException() {
        var actual = messageNotReadableDetails.getDetails(createJsonProcessingException());

        assertEquals("Some error at line: 20, column: 30 in json.", actual);
    }

    private JsonProcessingException createJsonProcessingException() {
        var jsonLocation = new JsonLocation("sourceRef", 10, 20, 30);

        return new JsonProcessingException("Some error", jsonLocation) {
        };
    }

    private static enum TestEnum {
        A, B
    }
}
