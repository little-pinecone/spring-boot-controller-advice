package in.keepgrowing.springbootcontrolleradvice.product.domain.model;

import dev.codesoapbox.dummy4j.Dummy4j;

public class TestProductProvider {

    private final Dummy4j dummy;

    public TestProductProvider() {
        this.dummy = new Dummy4j();
    }

    public Product full() {
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
