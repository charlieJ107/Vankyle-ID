package com.vankyle.id.data.entity;

import com.vankyle.id.data.utils.AuthorizationGrantTypeConverter;
import com.vankyle.id.data.utils.ObjectMapConverter;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

/**
 * JPA entity mapping the domain class(interface) {@link org.springframework.security.oauth2.server.authorization.OAuth2Authorization}
 */
@Data
@Entity
@Table(name = "oauth2_authorization")
public class Authorization {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String registeredClientId;
    private String principalName;
    private String state;
    @Convert(converter = AuthorizationGrantTypeConverter.class)
    private AuthorizationGrantType authorizationGrantType;
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> authorizedScopes;
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<OAuth2TokenData> tokens;
    @Column(columnDefinition = "text")
    @Convert(converter = ObjectMapConverter.class)
    private Map<String, Object> attributes;
    @Column(columnDefinition = "text")
    private String authorizationCode;
    private Instant authorizationCodeIssuedAt;
    private Instant authorizationCodeExpiresAt;
    @Column(columnDefinition = "text")
    @Convert(converter = ObjectMapConverter.class)
    private Map<String, Object> authorizationCodeMetadata;
    @Column(columnDefinition = "text")
    private String accessToken;
    private Instant accessTokenIssuedAt;
    private Instant accessTokenExpiresAt;
    @Column(columnDefinition = "text")
    @Convert(converter=ObjectMapConverter.class)
    private Map<String, Object> accessTokenMetadata;
    @Column(columnDefinition = "text")
    private String refreshToken;
    private Instant refreshTokenIssuedAt;
    private Instant refreshTokenExpiresAt;
    @Column(columnDefinition = "text")
    @Convert(converter=ObjectMapConverter.class)
    private Map<String, Object> refreshTokenMetadata;

    @Column(columnDefinition = "text")
    private String idToken;
    private Instant idTokenIssuedAt;
    private Instant idTokenExpiresAt;
    @Column(columnDefinition = "text")
    @Convert(converter=ObjectMapConverter.class)
    private Map<String, Object> idTokenMetadata;
    @Column(columnDefinition = "text")
    @Convert(converter=ObjectMapConverter.class)
    private Map<String, Object> idTokenClaims;
}
