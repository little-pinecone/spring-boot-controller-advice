package in.keepgrowing.springbootcontrolleradvice.product.presentation.controllers;

import in.keepgrowing.springbootcontrolleradvice.product.domain.model.Product;
import in.keepgrowing.springbootcontrolleradvice.product.domain.repositories.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "products", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProductController {

    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping()
    public ResponseEntity<List<Product>> findAll() {
        var products = productRepository.findAll();

        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Product> findById(@PathVariable UUID productId) {
        return productRepository.findById(productId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}
