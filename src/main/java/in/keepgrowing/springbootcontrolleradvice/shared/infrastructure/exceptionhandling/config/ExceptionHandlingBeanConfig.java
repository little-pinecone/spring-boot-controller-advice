package in.keepgrowing.springbootcontrolleradvice.shared.infrastructure.exceptionhandling.config;

import in.keepgrowing.springbootcontrolleradvice.shared.infrastructure.exceptionhandling.HttpMessageNotReadableDetailsProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExceptionHandlingBeanConfig {

    @Bean
    public HttpMessageNotReadableDetailsProvider httpMessageNotReadableDetailsProvider() {
        return new HttpMessageNotReadableDetailsProvider();
    }
}
