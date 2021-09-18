package in.keepgrowing.springbootcontrolleradvice.product.infrastructure.repositories;

import in.keepgrowing.springbootcontrolleradvice.product.domain.model.TestProductProvider;
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

    @Test
    void shouldSaveProduct() {
        var originalSize = productRepository.findAll().size();
        var productProvider = new TestProductProvider();
        var productDetails = productProvider.withoutId();

        var actual = productRepository.save(productDetails);

        assertNotNull(actual);
        assertTrue(actual.isPresent());
        assertNotNull(actual.get().getId());
        assertEquals(originalSize + 1, productRepository.findAll().size());
    }
}