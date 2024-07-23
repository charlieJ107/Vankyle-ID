package com.vankyle.id.data.entities;

import com.vankyle.id.data.converters.ObjectMapConverter;
import jakarta.persistence.*;
import lombok.Data;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Set;

/**
 * Entity for {@link org.springframework.security.oauth2.server.authorization.client.RegisteredClient}
 */
@Data
@Entity
public class ClientEntity {

    @Id
    private Long id;
    private String registeredClientId;
    private String clientId;
    private Instant clientIdIssuedAt;

    private String clientSecret;

    private Instant clientSecretExpiresAt;

    private String clientName;
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> clientAuthenticationMethods;
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> authorizationGrantTypes;
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> redirectUris;
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> postLogoutRedirectUris;
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> scopes;

    // Client settings
    private boolean requireProofKey;
    private boolean requireAuthorizationConsent;
    private String JwkSetUri;
    private String JwsAlgorithm;
    private String X509CertificateSubjectDN;
    @Column(columnDefinition = "text")
    @Convert(converter = ObjectMapConverter.class)
    private Map<String, Object> clientSettings;

    // Token settings
    private Duration authorizationCodeTimeToLive;
    private Duration accessTokenTimeToLive;
    private String accessTokenFormat;
    private Duration deviceCodeTimeToLive;
    private boolean isReuseRefreshToken;
    private Duration refreshTokenTimeToLive;
    private String idTokenSigningAlgorithm;
    private boolean X509CertificateBoundAccessTokens;
    @Column(columnDefinition = "text")
    @Convert(converter = ObjectMapConverter.class)
    private Map<String, Object> tokenSettings;


}
