package com.vankyle.id.data.entity;

import com.vankyle.id.data.utils.OAuth2TokenTypeConverter;
import com.vankyle.id.data.utils.ObjectMapConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;
import lombok.Data;
import org.springframework.security.oauth2.core.OAuth2Token;

import java.time.Instant;
import java.util.Map;

@Data
@Embeddable
public class OAuth2TokenData {
    @Column(columnDefinition = "text")
    private String tokenValue;
    @Convert(converter = OAuth2TokenTypeConverter.class)
    private Class<? extends OAuth2Token> tokenType;
    private Instant issuedAt;
    private Instant expiresAt;
    @Column(columnDefinition = "text")
    @Convert(converter = ObjectMapConverter.class)
    private Map<String, Object> attributes;
    @Column(columnDefinition = "text")
    @Convert(converter = ObjectMapConverter.class)
    private Map<String, Object> metadata;
    @Column(columnDefinition = "text")
    @Convert(converter = ObjectMapConverter.class)
    private Map<String, Object> claims;
}
