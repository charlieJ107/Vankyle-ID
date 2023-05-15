package com.vankyle.id.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "vankyle.id")
public class ApplicationProperties {
    @NonNull
    private String name = "Vankyle ID";
    @NonNull
    private String api_path = "/api";
    @NonNull
    private String base_url = "http://localhost:8080";
}
