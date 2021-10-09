package in.keepgrowing.springbootcontrolleradvice.shared.infrastructure.exceptionhandling;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;

@Data
public class HttpMessageNotReadableDetails {

    protected static final String UNKNOWN_TYPE = "type could not be established";
    protected static final String DEFAULT_MESSAGE = "The HTTP message could not be read.";

    private static final List<Class<?>> FLOATING_POINT = List.of(Double.class, double.class, Float.class, float.class);
    private static final List<Class<?>> INTEGER = List.of(Integer.class, int.class, Long.class, long.class,
            Short.class, short.class, Byte.class, byte.class);
    private static final List<Class<?>> DATE_TIME = List.of(Instant.class, ZonedDateTime.class, LocalDateTime.class);
    private static final Map<List<Class<?>>, String> MISMATCH_MESSAGES = Map.of(
            FLOATING_POINT, "a floating point number",
            INTEGER, "an integer number",
            List.of(String.class), "a string",
            List.of(Character.class, char.class), "a character",
            List.of(LocalDate.class), "a date",
            DATE_TIME, "a date and time",
            List.of(UUID.class), "a UUID",
            List.of(Boolean.class, boolean.class), "a boolean",
            List.of(List.class), "a list"
    );
    private static final String INVALID_VALUE_MESSAGE_FORMAT = "Invalid value '%s' for '%s' (expected %s) at %s.";
    private static final String INVALID_FIELD_MESSAGE_FORMAT = "Invalid value for '%s' (expected %s) at %s.";
    private static final String MISMATCH_ENUM_MESSAGE_FORMAT = "an enum '%s'";
    private static final String ROOT_PATH = "the request";
    private static final String SIMPLIFIED_LOCATION_FORMAT = "line: %d, column: %d in json";
    private static final String REFERENCE_INDEX_FORMAT = "[%s]";
    private static final String ERROR_PATH_DELIMITER = ".";
    private static final String JSON_PROCESSING_MESSAGE_FORMAT = "%s at %s.";

    public String getDetails(Throwable cause) {
        var errorDetails = DEFAULT_MESSAGE;

        if (cause instanceof InvalidFormatException) {
            errorDetails = createInvalidFormatErrorMessage((InvalidFormatException) cause);
        } else if (cause instanceof MismatchedInputException) {
            errorDetails = createMismatchedInputErrorMessage((MismatchedInputException) cause);
        } else if (cause instanceof JsonProcessingException) {
            errorDetails = createJsonProcessingErrorMessage((JsonProcessingException) cause);
        }

        return errorDetails;
    }

    private String createInvalidFormatErrorMessage(InvalidFormatException exception) {
        var value = exception.getValue();
        var errorPath = getErrorPath(exception);
        var expectedType = getSimplifiedRequiredType(exception.getTargetType());
        var locationInJson = getSimplifiedLocation(exception.getLocation());

        if (value == null) {
            return String.format(INVALID_FIELD_MESSAGE_FORMAT, errorPath, expectedType, locationInJson);
        }

        return String.format(INVALID_VALUE_MESSAGE_FORMAT, value, errorPath, expectedType, locationInJson);
    }

    private String getErrorPath(MismatchedInputException exception) {
        var references = exception.getPath();

        if (references.isEmpty()) {
            return ROOT_PATH;
        }

        var errorPath = buildPath(references);

        return String.join(ERROR_PATH_DELIMITER, errorPath);
    }

    private LinkedList<String> buildPath(List<JsonMappingException.Reference> references) {
        var errorPath = new LinkedList<String>();
        for (JsonMappingException.Reference reference : references) {
            if (reference.getFieldName() != null) {
                errorPath.add(reference.getFieldName());
            } else {
                var elementWithIndex = addIndexToElement(errorPath.getLast(), reference.getIndex());
                errorPath.removeLast();
                errorPath.add(elementWithIndex);
            }
        }
        return errorPath;
    }

    private String addIndexToElement(String lastPathElement, int referenceIndex) {
        var formattedIndex = String.format(REFERENCE_INDEX_FORMAT, referenceIndex);

        return lastPathElement + formattedIndex;
    }

    private String getSimplifiedRequiredType(Class<?> requiredType) {
        if (requiredType == null) {
            return "";
        }
        if (requiredType.isEnum()) {
            var s = Arrays.toString(requiredType.getEnumConstants());
            return String.format(MISMATCH_ENUM_MESSAGE_FORMAT, s);
        }

        return MISMATCH_MESSAGES.entrySet().stream()
                .filter(e -> e.getKey().stream().anyMatch(requiredType::isAssignableFrom))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElse(UNKNOWN_TYPE);
    }

    private String getSimplifiedLocation(JsonLocation location) {
        return String.format(SIMPLIFIED_LOCATION_FORMAT, location.getLineNr(), location.getColumnNr());
    }

    private String createMismatchedInputErrorMessage(MismatchedInputException exception) {
        var errorPath = getErrorPath(exception);
        var expectedType = getSimplifiedRequiredType(exception.getTargetType());
        var locationInJson = getSimplifiedLocation(exception.getLocation());

        return String.format(INVALID_FIELD_MESSAGE_FORMAT, errorPath, expectedType, locationInJson);
    }

    private String createJsonProcessingErrorMessage(JsonProcessingException procEx) {
        var message = procEx.getOriginalMessage();
        var location = getSimplifiedLocation(procEx.getLocation());

        return String.format(JSON_PROCESSING_MESSAGE_FORMAT, message, location);
    }
}
