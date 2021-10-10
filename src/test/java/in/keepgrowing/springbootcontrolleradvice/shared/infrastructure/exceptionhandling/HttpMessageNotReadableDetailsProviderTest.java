package in.keepgrowing.springbootcontrolleradvice.shared.infrastructure.exceptionhandling;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.http.MockHttpInputMessage;

import java.io.InputStream;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HttpMessageNotReadableDetailsProviderTest {

    private static final Pattern UNKNOWN_TYPE = Pattern.compile(SimpleTypeMapper.UNKNOWN_TYPE);

    private HttpMessageNotReadableDetailsProvider detailsProvider;
    private JsonParser parser;
    private MockHttpInputMessage inputMessage;

    @Mock
    private SimpleTypeMapper typeMapper;

    @BeforeEach
    void setUp() {
        detailsProvider = new HttpMessageNotReadableDetailsProvider(typeMapper);
        parser = Mockito.mock(JsonParser.class);
        lenient().when(parser.getTokenLocation())
                .thenReturn(new JsonLocation("sourceRef", 10, 20, 30));
        inputMessage = new MockHttpInputMessage(InputStream.nullInputStream());
    }

    @Test
    void shouldProvideDefaultDetailsWhenThereIsNoCause() {
        String actual = detailsProvider.getDetails(new HttpMessageNotReadableException("", inputMessage));

        assertEquals(HttpMessageNotReadableDetailsProvider.DEFAULT_MESSAGE, actual);
    }

    @Test
    void shouldProvideDetailsForInvalidFormatException() {
        var exception = InvalidFormatException.from(parser, "msg", "val", Double.class);
        prependPaths(exception);
        var expectedMessage = "Invalid value 'val' for 'field1[3].field2' (expected a floating point number) " +
                "at line: 20, column: 30 in json.";
        mockSimpleTypeMapper(Double.class, SimpleTypeMapper.SIMPLE_FLOATING);

        String actual = detailsProvider.getDetails(createParentException(exception));

        assertEquals(expectedMessage, actual);
    }

    private void mockSimpleTypeMapper(Class<?> required, String simplified) {
        when(typeMapper.map(required))
                .thenReturn(simplified);
    }

    private void prependPaths(JsonMappingException exception) {
        exception.prependPath(new JsonMappingException.Reference(null, "field2"));
        exception.prependPath(new JsonMappingException.Reference(null, 3));
        exception.prependPath(new JsonMappingException.Reference(null, "field1"));
    }

    private HttpMessageNotReadableException createParentException(Throwable innerException) {
        return new HttpMessageNotReadableException("msg", innerException, inputMessage);
    }

    @Test
    void shouldProvideDetailsForInvalidFormatExceptionWithoutValueAndPaths() {
        var exception = InvalidFormatException.from(parser, "msg", null, Double.class);
        var expectedMessage = "Invalid value for 'the request' (expected a floating point number) " +
                "at line: 20, column: 30 in json.";
        mockSimpleTypeMapper(Double.class, SimpleTypeMapper.SIMPLE_FLOATING);

        String actual = detailsProvider.getDetails(createParentException(exception));

        assertEquals(expectedMessage, actual);
    }

    @Test
    void shouldProvideDetailsForMismatchedInputExceptionWithPaths() {
        var exception = MismatchedInputException.from(parser, Double.class, "msg");
        prependPaths(exception);
        var expectedMessage = "Invalid value for 'field1[3].field2' (expected a floating point number) " +
                "at line: 20, column: 30 in json.";
        mockSimpleTypeMapper(Double.class, SimpleTypeMapper.SIMPLE_FLOATING);

        String actual = detailsProvider.getDetails(createParentException(exception));

        assertEquals(expectedMessage, actual);
    }

    @Test
    void shouldProvideDetailsForMismatchedInputExceptionWhenRequiredTypeIsNotSupported() {
        var exception = MismatchedInputException.from(parser, Map.class, "msg");
        mockSimpleTypeMapper(Map.class, SimpleTypeMapper.UNKNOWN_TYPE);

        String actual = detailsProvider.getDetails(createParentException(exception));
        Matcher bicMatcher = UNKNOWN_TYPE.matcher(actual);

        assertTrue(bicMatcher.find());
    }

    @Test
    void shouldProvideDetailsForMismatchedInputExceptionForEnums() {
        var exception = MismatchedInputException.from(parser, TestEnum.class, "msg");
        var expectedMessage = "Invalid value for 'the request' (expected an enum '[A, B]') at line: 20, column: 30 in json.";
        mockSimpleTypeMapper(TestEnum.class, String.format(SimpleTypeMapper.SIMPLE_ENUM, "[A, B]"));

        String actual = detailsProvider.getDetails(createParentException(exception));

        assertEquals(expectedMessage, actual);
    }

    @Test
    void shouldProvideDetailsForMismatchedInputExceptionWithEmptyPath() {
        var exception = MismatchedInputException.from(parser, int.class, "msg");
        var expectedMessage = "Invalid value for 'the request' (expected an integer number) at line: 20, column: 30 in json.";
        mockSimpleTypeMapper(int.class, SimpleTypeMapper.SIMPLE_INTEGER);

        String actual = detailsProvider.getDetails(createParentException(exception));

        assertEquals(expectedMessage, actual);
    }

    @Test
    void shouldProvideDetailsForJsonProcessingException() {
        JsonProcessingException exception = createJsonProcessingException();

        String actual = detailsProvider.getDetails(createParentException(exception));

        assertEquals("Some error at line: 20, column: 30 in json.", actual);
    }

    private JsonProcessingException createJsonProcessingException() {
        var jsonLocation = new JsonLocation("sourceRef", 10, 20, 30);

        return new JsonProcessingException("Some error", jsonLocation) {
        };
    }

    private enum TestEnum {
        A, B
    }
}
