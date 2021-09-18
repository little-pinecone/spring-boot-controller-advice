package in.keepgrowing.springbootcontrolleradvice.product.domain.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.UUID;

@Data
@Builder
public class Product {

    private UUID id;
    private String name;
    private String color;

    @NotBlank
    private String ean;
    private String countryOfOrigin;
    private String price;
    private int availableQuantity;
}
