package in.keepgrowing.springbootcontrolleradvice.shared.infrastructure.exceptionhandling;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import lombok.Data;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.util.LinkedList;
import java.util.List;

@Data
public class HttpMessageNotReadableDetailsProvider {

    protected static final String DEFAULT_MESSAGE = "The HTTP message could not be read.";

    private static final String INVALID_VALUE_MESSAGE_FORMAT = "Invalid value '%s' for '%s' (expected %s) at %s.";
    private static final String INVALID_FIELD_MESSAGE_FORMAT = "Invalid value for '%s' (expected %s) at %s.";
    private static final String ROOT_PATH = "the request";
    private static final String SIMPLIFIED_LOCATION_FORMAT = "line: %d, column: %d in json";
    private static final String REFERENCE_INDEX_FORMAT = "[%s]";
    private static final String ERROR_PATH_DELIMITER = ".";
    private static final String JSON_PROCESSING_MESSAGE_FORMAT = "%s at %s.";

    private final SimpleTypeMapper simpleTypeMapper;

    public HttpMessageNotReadableDetailsProvider(SimpleTypeMapper simpleTypeMapper) {
        this.simpleTypeMapper = simpleTypeMapper;
    }

    public String getDetails(HttpMessageNotReadableException exception) {
        Throwable cause = exception.getCause();

        if (cause instanceof InvalidFormatException) {
            return createInvalidFormatErrorMessage((InvalidFormatException) cause);
        } else if (cause instanceof MismatchedInputException) {
            return createMismatchedInputErrorMessage((MismatchedInputException) cause);
        } else if (cause instanceof JsonProcessingException) {
            return createJsonProcessingErrorMessage((JsonProcessingException) cause);
        }

        return DEFAULT_MESSAGE;
    }

    private String createInvalidFormatErrorMessage(InvalidFormatException exception) {
        Object value = exception.getValue();
        String errorPath = getErrorPath(exception);
        String expectedType = simpleTypeMapper.map(exception.getTargetType());
        String locationInJson = getSimplifiedLocation(exception.getLocation());

        if (value == null) {
            return String.format(INVALID_FIELD_MESSAGE_FORMAT, errorPath, expectedType, locationInJson);
        }

        return String.format(INVALID_VALUE_MESSAGE_FORMAT, value, errorPath, expectedType, locationInJson);
    }

    private String getErrorPath(MismatchedInputException exception) {
        List<JsonMappingException.Reference> references = exception.getPath();

        if (references.isEmpty()) {
            return ROOT_PATH;
        }

        LinkedList<String> errorPath = buildPath(references);

        return String.join(ERROR_PATH_DELIMITER, errorPath);
    }

    private LinkedList<String> buildPath(List<JsonMappingException.Reference> references) {
        LinkedList<String> errorPath = new LinkedList<>();
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
        String formattedIndex = String.format(REFERENCE_INDEX_FORMAT, referenceIndex);

        return lastPathElement + formattedIndex;
    }

    private String getSimplifiedLocation(JsonLocation location) {
        return String.format(SIMPLIFIED_LOCATION_FORMAT, location.getLineNr(), location.getColumnNr());
    }

    private String createMismatchedInputErrorMessage(MismatchedInputException exception) {
        String errorPath = getErrorPath(exception);
        String expectedType = simpleTypeMapper.map(exception.getTargetType());
        String locationInJson = getSimplifiedLocation(exception.getLocation());

        return String.format(INVALID_FIELD_MESSAGE_FORMAT, errorPath, expectedType, locationInJson);
    }

    private String createJsonProcessingErrorMessage(JsonProcessingException procEx) {
        String message = procEx.getOriginalMessage();
        String location = getSimplifiedLocation(procEx.getLocation());

        return String.format(JSON_PROCESSING_MESSAGE_FORMAT, message, location);
    }
}
