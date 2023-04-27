package com.vankyle.id.data.entity;

import com.vankyle.id.data.utils.JwsAlgorithmConverter;
import com.vankyle.id.data.utils.ObjectMapConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;
import lombok.Data;
import org.springframework.security.oauth2.jose.jws.JwsAlgorithm;

import java.util.Map;

@Data
@Embeddable
public class ClientSettingsData {
    private boolean isRequireProofKey;
    private boolean isRequireAuthorizationConsent;
    private String JwkSetUri;
    @Convert(converter= ObjectMapConverter.class)
    @Column(columnDefinition = "text")
    private Map<String, Object> clientSettings;
    @Convert(converter = JwsAlgorithmConverter.class)
    private JwsAlgorithm tokenEndpointAuthenticationSigningAlgorithm;
}
