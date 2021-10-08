package in.keepgrowing.springbootcontrolleradvice.shared.infrastructure.exceptionhandling.config;

import in.keepgrowing.springbootcontrolleradvice.shared.infrastructure.exceptionhandling.HttpMessageNotReadableDetails;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExceptionHandlingBeanConfig {

    @Bean
    public HttpMessageNotReadableDetails httpMessageNotReadableDetails() {
        return new HttpMessageNotReadableDetails();
    }
}
