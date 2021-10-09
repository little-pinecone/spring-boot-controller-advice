package in.keepgrowing.springbootcontrolleradvice.product.presentation.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.keepgrowing.springbootcontrolleradvice.product.domain.model.Product;
import in.keepgrowing.springbootcontrolleradvice.product.domain.model.TestProductProvider;
import in.keepgrowing.springbootcontrolleradvice.product.domain.repositories.ProductRepository;
import in.keepgrowing.springbootcontrolleradvice.product.infrastructure.config.MvcConfig;
import in.keepgrowing.springbootcontrolleradvice.shared.infrastructure.exceptionhandling.HttpMessageNotReadableDetailsProvider;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    @MockBean
    private HttpMessageNotReadableDetailsProvider messageNotReadableDetails;

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
        Product product = productProvider.full();
        List<Product> products = List.of(product);

        when(productRepository.findAll())
                .thenReturn(products);

        String expectedResponse = objectMapper.writeValueAsString(products);

        mvc.perform(get(BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    void shouldReturnProductById() throws Exception {
        Product product = productProvider.full();

        when(productRepository.findById(UUID.fromString(TEST_UUID)))
                .thenReturn(Optional.of(product));

        String expectedResponse = objectMapper.writeValueAsString(product);

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

    @Test
    void shouldSaveNewProduct() throws Exception {
        Product productDetails = productProvider.withoutId();
        Product expected = productProvider.full();

        when(productRepository.save(productDetails))
                .thenReturn(Optional.of(expected));

        String expectedResponse = objectMapper.writeValueAsString(expected);

        mvc.perform(post(BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDetails)))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    void shouldReturnBadRequestForInvalidProductDetails() throws Exception {
        Product productDetails = productProvider.withoutId();
        productDetails.setEan("");
        String content = objectMapper.writeValueAsString(productDetails);

        mvc.perform(post(BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void shouldReturnInternalServerErrorOnRepositoryFailure() throws Exception {
        Product productDetails = productProvider.withoutId();
        String content = objectMapper.writeValueAsString(productDetails);

        when(productRepository.save(productDetails))
                .thenReturn(Optional.empty());

        mvc.perform(post(BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldReturnBadRequestForMessageNotReadable() throws Exception {
        Product productDetails = productProvider.withoutId();
        String invalidContent = "[" + objectMapper.writeValueAsString(productDetails) + "]";

        mvc.perform(post(BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidContent))
                .andExpect(status().isBadRequest());
    }
}