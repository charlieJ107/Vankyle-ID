package com.vankyle.id.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "vankyle.id")
public class ApplicationProperties {
    @NonNull
    private String base_url = "http://localhost:8080";
    @Nullable
    private String frontend_url = "http://localhost:3000";
    @NonNull
    private boolean restful = true;
    @NonNull
    private boolean integrated = true;
    @Nullable
    private long email_verification_code_expiry;
    @Nullable
    private int email_verification_code_length;
    @Nullable
    private long phone_verification_code_expiry;
    @Nullable
    private int phone_verification_code_length;
    @Nullable
    private long totp_code_expiry;
    @Nullable
    private int totp_code_length;

}
