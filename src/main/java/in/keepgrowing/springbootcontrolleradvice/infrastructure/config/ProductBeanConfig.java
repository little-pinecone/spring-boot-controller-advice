package in.keepgrowing.springbootcontrolleradvice.infrastructure.config;

import in.keepgrowing.springbootcontrolleradvice.domain.repositories.ProductRepository;
import in.keepgrowing.springbootcontrolleradvice.infrastructure.repositories.InMemoryProductRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProductBeanConfig {

    @Bean
    public ProductRepository productRepository() {
        return new InMemoryProductRepository();
    }
}
