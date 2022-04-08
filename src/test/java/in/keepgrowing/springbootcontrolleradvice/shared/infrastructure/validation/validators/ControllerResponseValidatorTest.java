package in.keepgrowing.springbootcontrolleradvice.shared.infrastructure.validation.validators;

import in.keepgrowing.springbootcontrolleradvice.product.domain.model.Product;
import in.keepgrowing.springbootcontrolleradvice.product.domain.model.TestProductProvider;
import in.keepgrowing.springbootcontrolleradvice.shared.infrastructure.validation.config.ValidationBeanConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.NestedServletException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest(classes = {ControllerResponseValidatorTest.TestController.class, ValidationBeanConfig.class})
@EnableAspectJAutoProxy
class ControllerResponseValidatorTest {

    private static final String PATH = "/test";
    private static final String PATH_FOR_LIST = "/list";
    private static final String PATH_FOR_NULL_RESPONSE = "/null";

    private MockMvc mvc;

    @Autowired
    private ControllerResponseValidatorTest.TestController testController;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(testController).build();
    }

    @Test
    void shouldThrowExceptionOnInvalidObjectInResponse() {
        assertThrows(NestedServletException.class, () -> mvc.perform(get(PATH)));
    }

    @Test
    void shouldThrowExceptionOnInvalidCollectionInResponse() {
        assertThrows(NestedServletException.class, () -> mvc.perform(get(PATH_FOR_LIST)));
    }

    @Test
    void shouldThrowExceptionOnNullResponseBody() {
        assertThrows(NestedServletException.class, () -> mvc.perform(get(PATH)));
    }

    @RestController
    @Slf4j
    static class TestController {

        @GetMapping(PATH)
        public ResponseEntity<Product> executeTestRequest() {
            return ResponseEntity.ok(getInvalidProduct());
        }

        private Product getInvalidProduct() {
            var productProvider = new TestProductProvider();
            Product product = productProvider.full();
            product.getManufacturer().setContactEmail("");

            return product;
        }

        @GetMapping(PATH_FOR_LIST)
        public ResponseEntity<List<Product>> executeTestRequestForList() {
            return ResponseEntity.ok(List.of(getInvalidProduct()));
        }

        @GetMapping(PATH_FOR_NULL_RESPONSE)
        public ResponseEntity<List<Product>> executeTestRequestForNullResponse() {
            return ResponseEntity.ok(null);
        }
    }
}