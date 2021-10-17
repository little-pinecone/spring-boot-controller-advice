package in.keepgrowing.springbootcontrolleradvice.product.presentation.controllers;

import in.keepgrowing.springbootcontrolleradvice.product.domain.model.Product;
import in.keepgrowing.springbootcontrolleradvice.product.domain.repositories.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "products", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
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

    @GetMapping("{productId}")
    public ResponseEntity<Product> findById(@PathVariable UUID productId) {
        return productRepository.findById(productId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping()
    public ResponseEntity<Product> save(@Valid @RequestBody Product productDetails) {
        return productRepository.save(productDetails)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    @GetMapping("filter")
    public ResponseEntity<List<Product>> findByMinimumQuantity(@RequestParam("quantity") @Min(1) int quantity) {
        var products = productRepository.findByMinimumQuantity(quantity);

        return ResponseEntity.ok(products);
    }
}
