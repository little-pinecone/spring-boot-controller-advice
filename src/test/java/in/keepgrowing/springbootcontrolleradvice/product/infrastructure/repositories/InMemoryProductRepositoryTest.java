package in.keepgrowing.springbootcontrolleradvice.product.infrastructure.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryProductRepositoryTest {

    private InMemoryProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository = new InMemoryProductRepository();
    }

    @Test
    void shouldReturnListOfProducts() {
        var actual = productRepository.findAll();

        assertNotNull(actual);

        assertAll(
                () -> assertFalse(actual.isEmpty(), "Empty list"),
                () -> assertNotNull(actual.get(0), "List element is null"),
                () -> assertFalse(actual.get(0).getName().isBlank(), "Blank element field")
        );
    }

    @Test
    void shouldReturnProductById() {
        var products = productRepository.findAll();

        var actual = productRepository.findById(products.get(0).getId());

        assertNotNull(actual);
        assertTrue(actual.isPresent());
        assertNotNull(actual.get().getId());
    }
}