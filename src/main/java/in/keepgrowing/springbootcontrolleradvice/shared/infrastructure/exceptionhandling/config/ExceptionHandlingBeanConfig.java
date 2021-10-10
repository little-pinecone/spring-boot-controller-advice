package in.keepgrowing.springbootcontrolleradvice.shared.infrastructure.exceptionhandling.config;

import in.keepgrowing.springbootcontrolleradvice.shared.infrastructure.exceptionhandling.HttpMessageNotReadableDetailsProvider;
import in.keepgrowing.springbootcontrolleradvice.shared.infrastructure.exceptionhandling.SimpleTypeMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExceptionHandlingBeanConfig {

    @Bean
    public HttpMessageNotReadableDetailsProvider httpMessageNotReadableDetailsProvider(SimpleTypeMapper typeMapper) {
        return new HttpMessageNotReadableDetailsProvider(typeMapper);
    }

    @Bean
    public SimpleTypeMapper simpleTypeMapper() {
        return new SimpleTypeMapper();
    }
}
