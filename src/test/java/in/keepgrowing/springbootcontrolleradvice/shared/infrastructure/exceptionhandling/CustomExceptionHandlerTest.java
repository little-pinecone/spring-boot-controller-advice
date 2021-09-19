package in.keepgrowing.springbootcontrolleradvice.shared.infrastructure.exceptionhandling;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
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

    @RestController
    private static class TestController {

        @GetMapping(PATH)
        public String executeTestRequest() {
            return null;
        }
    }
}