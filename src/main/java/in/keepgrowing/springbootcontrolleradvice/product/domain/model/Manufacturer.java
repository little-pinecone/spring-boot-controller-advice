package in.keepgrowing.springbootcontrolleradvice.product.domain.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
public class Manufacturer {

    private String name;

    @Email
    @NotBlank
    private String contactEmail;
}
