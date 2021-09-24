package in.keepgrowing.springbootcontrolleradvice.shared.infrastructure.exceptionhandling;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.io.InputStream;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CustomExceptionHandlerTest {

    private static final String PATH = "/test";

    private MockMvc mvc;

    @Mock
    private TestController testController;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(testController)
                .setControllerAdvice(new CustomExceptionHandler())
                .build();
    }

    @Test
    void shouldHandleRuntimeException() throws Exception {
        var expected = ExceptionCode.INTERNAL_SERVER_ERROR.toString();

        when(testController.executeTestRequest())
                .thenThrow(RuntimeException.class);

        mvc.perform(get(PATH))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.exceptionCode", is(expected)));
    }

    @Test
    void shouldHandleMethodArgumentTypeMismatchException() throws Exception {
        var expected = ExceptionCode.CLIENT_ERROR.toString();

        when(testController.executeTestRequest())
                .thenThrow(MethodArgumentTypeMismatchException.class);

        mvc.perform(get(PATH))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exceptionCode", is(expected)));
    }

    @Test
    void shouldHandleMethodArgumentNotValid() throws Exception {
        var expected = ExceptionCode.CLIENT_ERROR.toString();
        var exception = mock((MethodArgumentNotValidException.class));

        doAnswer(invocation -> {
            throw exception;
        }).when(testController).executeTestRequest();

        mvc.perform(get(PATH))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.exceptionCode", is(expected)));
    }

    @Test
    void shouldHandleJsonProcessingException() throws Exception {
        var expectedCode = ExceptionCode.CLIENT_ERROR.toString();
        var expectedMessage = "Invalid request. Some error at line: 20, column: 30";

        when(testController.executeTestRequest())
                .thenThrow(createHttpMessageNotReadableException());

        mvc.perform(get("/test"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exceptionCode", is(expectedCode)))
                .andExpect(jsonPath("$.message", is(expectedMessage)));
    }

    private HttpMessageNotReadableException createHttpMessageNotReadableException() {
        var innerException = createJsonProcessingException();
        var inputMessage = new MockHttpInputMessage(InputStream.nullInputStream());

        return new HttpMessageNotReadableException("Unexpected Exception", innerException, inputMessage);
    }

    private JsonProcessingException createJsonProcessingException() {
        var jsonLocation = new JsonLocation("sourceRef", 10, 20, 30);

        return new JsonProcessingException("Some error", jsonLocation) {
        };
    }

    @RestController
    private static class TestController {

        @GetMapping(PATH)
        public String executeTestRequest() {
            return null;
        }
    }
}