package in.keepgrowing.springbootcontrolleradvice.product.domain.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class Manufacturer {

    private String name;

    @Email
    @NotNull
    private String contactEmail;
}
