package com.vankyle.id.data.entities;

import com.vankyle.id.data.converters.ObjectMapConverter;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

/**
 * The entity class for the {@link OAuth2Authorization}
 */
@Data
@Entity
public class AuthorizationEntity {
    @Id
    @GeneratedValue
    private long id;
    private String authorizationId;
    private String registeredClientId;
    private String principalName;
    private String state;

    /**
     * Storage the value filed of {@link AuthorizationGrantType}
     */
    private String authorizationGrantType;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> authorizedScopes;

    @Column(columnDefinition = "text")
    @Convert(converter = ObjectMapConverter.class)
    private Map<String, Object> attributes;

    @Column(columnDefinition = "text")
    private String authorizationCodeValue;
    private Instant authorizationCodeIssuedAt;
    private Instant authorizationCodeExpiresAt;
    @Column(columnDefinition = "text")
    @Convert(converter = ObjectMapConverter.class)
    private Map<String, Object> authorizationCodeMetadata;

    @Column(columnDefinition = "text")
    private String accessTokenValue;
    private Instant accessTokenIssuedAt;
    private Instant accessTokenExpiresAt;
    private String accessTokenType;
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> accessTokenScopes;
    @Column(columnDefinition = "text")
    @Convert(converter = ObjectMapConverter.class)
    private Map<String, Object> accessTokenMetadata;

    @Column(columnDefinition = "text")
    private String refreshTokenValue;
    private Instant refreshTokenIssuedAt;
    private Instant refreshTokenExpiresAt;
    @Column(columnDefinition = "text")
    @Convert(converter = ObjectMapConverter.class)
    private Map<String, Object> refreshTokenMetadata;

    @Column(columnDefinition = "text")
    private String oidcIdTokenValue;
    private Instant oidcIdTokenIssuedAt;
    private Instant oidcIdTokenExpiresAt;
    @Column(columnDefinition = "text")
    @Convert(converter = ObjectMapConverter.class)
    private Map<String, Object> oidcIdTokenMetadata;
    @Column(columnDefinition = "text")
    @Convert(converter = ObjectMapConverter.class)
    private Map<String, Object> oidcIdTokenClaims;

    @Column(columnDefinition = "text")
    private String userCodeValue;
    private Instant userCodeIssuedAt;
    private Instant userCodeExpiresAt;
    @Column(columnDefinition = "text")
    @Convert(converter = ObjectMapConverter.class)
    private Map<String, Object> userCodeMetadata;

    @Column(columnDefinition = "text")
    private String deviceCodeValue;
    private Instant deviceCodeIssuedAt;
    private Instant deviceCodeExpiresAt;
    @Column(columnDefinition = "text")
    @Convert(converter = ObjectMapConverter.class)
    private Map<String, Object> deviceCodeMetadata;
}
