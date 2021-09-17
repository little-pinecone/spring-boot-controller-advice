package in.keepgrowing.springbootcontrolleradvice.domain.repositories;

import in.keepgrowing.springbootcontrolleradvice.domain.model.Product;

import java.util.List;

public interface ProductRepository {

    List<Product> findAll();
}
