package in.keepgrowing.springbootcontrolleradvice.infrastructure.repositories;

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
}