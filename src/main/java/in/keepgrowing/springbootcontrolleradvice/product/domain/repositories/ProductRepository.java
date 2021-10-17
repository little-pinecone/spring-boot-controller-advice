package in.keepgrowing.springbootcontrolleradvice.product.domain.repositories;

import in.keepgrowing.springbootcontrolleradvice.product.domain.model.Product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {

    List<Product> findAll();

    Optional<Product> findById(UUID productId);

    Optional<Product> save(Product productDetails);

    List<Product> findByMinimumQuantity(int quantity);
}
