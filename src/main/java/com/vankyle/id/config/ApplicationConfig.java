package com.vankyle.id.config;

import com.vankyle.id.config.properties.ApplicationProperties;
import com.vankyle.id.data.repository.UserRepository;
import com.vankyle.id.service.validation.DefaultValidationService;
import com.vankyle.id.service.validation.ValidationService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ApplicationProperties.class)
public class ApplicationConfig {
    @Bean
    ValidationService validationService(UserRepository userRepository) {
        return new DefaultValidationService(userRepository);
    }
}
