package in.keepgrowing.springbootcontrolleradvice.product.presentation.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.keepgrowing.springbootcontrolleradvice.product.domain.model.TestProductProvider;
import in.keepgrowing.springbootcontrolleradvice.product.domain.repositories.ProductRepository;
import in.keepgrowing.springbootcontrolleradvice.product.infrastructure.config.MvcConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest(value = ProductController.class)
class ProductControllerTest {

    private static final String BASE_PATH = "/" + MvcConfig.API_PREFIX + "/products";
    private static final String TEST_UUID = "a25fd1a8-b2e2-3b40-97a5-cead9ec87986";

    private TestProductProvider productProvider;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductController controller;

    @MockBean
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productProvider = new TestProductProvider();
    }

    @Test
    void contextLoads() {
        assertNotNull(controller);
    }

    @Test
    void shouldReturnAllProducts() throws Exception {
        var product = productProvider.full();
        var products = List.of(product);

        when(productRepository.findAll())
                .thenReturn(products);

        var expectedResponse = objectMapper.writeValueAsString(products);

        mvc.perform(get(BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    void shouldReturnProductById() throws Exception {
        var product = productProvider.full();

        when(productRepository.findById(UUID.fromString(TEST_UUID)))
                .thenReturn(Optional.of(product));

        var expectedResponse = objectMapper.writeValueAsString(product);

        mvc.perform(get(BASE_PATH + "/" + TEST_UUID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    void shouldReturnNotFoundForNonExistingId() throws Exception {
        when(productRepository.findById(UUID.fromString(TEST_UUID)))
                .thenReturn(Optional.empty());

        mvc.perform(get(BASE_PATH + "/" + TEST_UUID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}