package com.vankyle.id.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
import org.springframework.lang.NonNull;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "vankyle.id.mail")
public class EmailProperties {
    @NonNull
    private boolean enabled;
    @NonNull
    private String base_url;
    @Nullable
    private String from;

    @Nullable
    private String[] cc;

    @Nullable
    private String[] bcc;
}
