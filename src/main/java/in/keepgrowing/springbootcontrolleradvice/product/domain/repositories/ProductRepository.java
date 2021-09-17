package in.keepgrowing.springbootcontrolleradvice.product.domain.repositories;

import in.keepgrowing.springbootcontrolleradvice.product.domain.model.Product;

import java.util.List;

public interface ProductRepository {

    List<Product> findAll();
}
