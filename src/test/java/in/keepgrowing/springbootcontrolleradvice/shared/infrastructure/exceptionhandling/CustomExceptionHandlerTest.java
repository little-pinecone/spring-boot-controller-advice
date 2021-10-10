package in.keepgrowing.springbootcontrolleradvice.shared.infrastructure.exceptionhandling;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

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

    @Mock
    private HttpMessageNotReadableDetailsProvider messageNotReadableDetailsProvider;

    @Mock
    private SimpleTypeMapper simpleTypeMapper;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(testController)
                .setControllerAdvice(new CustomExceptionHandler(messageNotReadableDetailsProvider, simpleTypeMapper))
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
        var expectedMessage = "Invalid request. Invalid value for 'field' (expected an integer number).";
        var exception = mock(MethodArgumentTypeMismatchException.class);

        when(testController.executeTestRequest())
                .thenThrow(exception);

        when(exception.getName())
                .thenReturn("field");

        when(simpleTypeMapper.map(any()))
                .thenReturn(SimpleTypeMapper.SIMPLE_INTEGER);

        mvc.perform(get(PATH))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exceptionCode", is(expected)))
                .andExpect(jsonPath("$.message", is(expectedMessage)));
    }

    @Test
    void shouldHandleMethodArgumentNotValid() throws Exception {
        var expected = ExceptionCode.CLIENT_ERROR.toString();
        var exception = mock(MethodArgumentNotValidException.class);

        when(exception.getFieldErrors())
                .thenReturn(List.of(new FieldError("object", "field", "msg")));

        doAnswer(invocation -> {
            throw exception;
        }).when(testController).executeTestRequest();

        mvc.perform(get(PATH))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.exceptionCode", is(expected)))
                .andExpect(jsonPath("$.validationErrors[*].fieldName", Matchers.contains("field")))
                .andExpect(jsonPath("$.validationErrors[*].brokenConstraint", Matchers.contains("msg")));
    }

    @Test
    void shouldHandleHttpMessageNotReadableException() throws Exception {
        var expectedCode = ExceptionCode.CLIENT_ERROR.toString();
        var expectedMessage = "Invalid request. Error details";

        when(testController.executeTestRequest())
                .thenThrow(HttpMessageNotReadableException.class);

        when(messageNotReadableDetailsProvider.getDetails(any()))
                .thenReturn("Error details");

        mvc.perform(get("/test"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exceptionCode", is(expectedCode)))
                .andExpect(jsonPath("$.message", is(expectedMessage)));
    }

    @RestController
    private static class TestController {

        @GetMapping(PATH)
        public String executeTestRequest() {
            return null;
        }
    }
}