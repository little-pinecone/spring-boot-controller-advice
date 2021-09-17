package in.keepgrowing.springbootcontrolleradvice.product.infrastructure.repositories;

import dev.codesoapbox.dummy4j.Dummy4j;
import in.keepgrowing.springbootcontrolleradvice.product.domain.model.Product;
import in.keepgrowing.springbootcontrolleradvice.product.domain.repositories.ProductRepository;

import java.util.List;

public class InMemoryProductRepository implements ProductRepository {

    private final Dummy4j dummy;

    public InMemoryProductRepository() {
        this.dummy = new Dummy4j();
    }

    @Override
    public List<Product> findAll() {
        return dummy.listOf(20, this::generateProduct);
    }

    private Product generateProduct() {
        return Product.builder()
                .id(dummy.identifier().uuid())
                .name(dummy.lorem().word() + " " + dummy.lorem().word())
                .color(dummy.color().name())
                .ean(dummy.identifier().ean13())
                .countryOfOrigin(dummy.nation().country())
                .price(dummy.finance().priceBuilder().withCurrency("USD").build())
                .availableQuantity(dummy.number().nextInt(1, 200))
                .build();
    }
}
