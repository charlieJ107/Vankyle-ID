package com.vankyle.id.data.entity;

import com.vankyle.id.data.utils.JwsAlgorithmConverter;
import com.vankyle.id.data.utils.ObjectMapConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;
import lombok.Data;
import org.springframework.security.oauth2.jose.jws.JwsAlgorithm;

import java.time.Duration;
import java.util.Map;

@Data
@Embeddable
public class TokenSettingsData {
    private Duration authorizationCodeTimeToLive;
    private Duration accessTokenTimeToLive;
    private String accessTokenFormat;
    private boolean reuseRefreshTokens;
    private Duration refreshTokenTimeToLive;
    @Convert(converter = JwsAlgorithmConverter.class)
    private JwsAlgorithm idTokenSignatureAlgorithm;
    @Column(columnDefinition = "text")
    @Convert(converter= ObjectMapConverter.class)
    private Map<String, Object> tokenSettings;
}
