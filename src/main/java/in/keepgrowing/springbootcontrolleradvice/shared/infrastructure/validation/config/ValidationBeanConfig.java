package in.keepgrowing.springbootcontrolleradvice.shared.infrastructure.validation.config;

import in.keepgrowing.springbootcontrolleradvice.shared.infrastructure.validation.validators.ControllerResponseValidator;
import in.keepgrowing.springbootcontrolleradvice.shared.infrastructure.validation.validators.JavaxThrowingValidator;
import in.keepgrowing.springbootcontrolleradvice.shared.domain.validation.ThrowingValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ValidationBeanConfig {

    @Bean
    public ThrowingValidator throwingValidator() {
        return new JavaxThrowingValidator();
    }

    @Bean
    public ControllerResponseValidator controllerResponseValidator(ThrowingValidator validator) {
        return new ControllerResponseValidator(validator);
    }
}
