package in.keepgrowing.springbootcontrolleradvice.product.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.Validation;

import static org.junit.jupiter.api.Assertions.assertFalse;

class ProductValidationTest {

    private Product product;

    @BeforeEach
    void setUp() {
        var productProvider = new TestProductProvider();
        product = productProvider.full();
    }

    @Test
    void shouldApplyValidationToFields() {
        var validator = Validation.buildDefaultValidatorFactory().getValidator();
        product.setEan("");

        var violations = validator.validate(product);

        assertFalse(violations.isEmpty());
    }
}