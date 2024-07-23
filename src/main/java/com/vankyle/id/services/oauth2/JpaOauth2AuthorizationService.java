package com.vankyle.id.services.oauth2;

import com.vankyle.id.data.entities.AuthorizationEntity;
import com.vankyle.id.data.repositories.AuthorizationRepository;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.util.Assert;

import java.util.Optional;

public class JpaOauth2AuthorizationService implements OAuth2AuthorizationService {
    private final AuthorizationRepository authorizationRepository;
    private final RegisteredClientRepository registeredClientRepository;

    public JpaOauth2AuthorizationService(
            AuthorizationRepository authorizationRepository,
            RegisteredClientRepository registeredClientRepository
    ) {
        this.authorizationRepository = authorizationRepository;
        this.registeredClientRepository = registeredClientRepository;
    }

    @Override
    public void save(OAuth2Authorization authorization) {
        Assert.notNull(authorization, "authorization cannot be null");
        AuthorizationEntity entity = authorizationRepository.findByAuthorizationId(authorization.getId()).orElse(new AuthorizationEntity());
        entity = toEntity(authorization, entity);
        authorizationRepository.save(entity);
    }

    @Override
    public void remove(OAuth2Authorization authorization) {
        Assert.notNull(authorization, "authorization cannot be null");
        authorizationRepository.findByAuthorizationId(authorization.getId()).ifPresent(authorizationRepository::delete);
    }

    @Override
    public OAuth2Authorization findById(String id) {
        Assert.hasText(id, "id cannot be empty");
        AuthorizationEntity entity = authorizationRepository.findByAuthorizationId(id).orElse(null);
        return entity == null ? null : toObject(entity);
    }

    @Override
    public OAuth2Authorization findByToken(String token, OAuth2TokenType tokenType) {
        Assert.hasText(token, "token cannot be empty");
        if (tokenType == null) {
            return this.authorizationRepository.findByToken(token)
                    .map(this::toObject)
                    .orElse(null);
        }
        Optional<AuthorizationEntity> entityOptional = switch (tokenType.getValue()) {
            case OAuth2ParameterNames.STATE -> this.authorizationRepository.findByState(token);
            case OAuth2ParameterNames.CODE -> this.authorizationRepository.findByAuthorizationCodeValue(token);
            case OAuth2ParameterNames.ACCESS_TOKEN -> this.authorizationRepository.findByAccessTokenValue(token);
            case OAuth2ParameterNames.REFRESH_TOKEN -> this.authorizationRepository.findByRefreshTokenValue(token);
            case OidcParameterNames.ID_TOKEN -> this.authorizationRepository.findByOidcIdTokenValue(token);
            case OAuth2ParameterNames.USER_CODE -> this.authorizationRepository.findByUserCodeValue(token);
            case OAuth2ParameterNames.DEVICE_CODE -> this.authorizationRepository.findByDeviceCodeValue(token);
            default -> Optional.empty();
        };
        return entityOptional.map(this::toObject).orElse(null);
    }

    private AuthorizationEntity toEntity(OAuth2Authorization authorization, AuthorizationEntity entity) {
        entity.setAuthorizationId(authorization.getId());
        entity.setRegisteredClientId(authorization.getRegisteredClientId());
        entity.setPrincipalName(authorization.getPrincipalName());
        entity.setAuthorizationGrantType(authorization.getAuthorizationGrantType().getValue());
        entity.setAuthorizedScopes(authorization.getAuthorizedScopes());
        entity.setAttributes(authorization.getAttributes());
        entity.setState(authorization.getAttribute(OAuth2ParameterNames.STATE));
        OAuth2Authorization.Token<OAuth2AccessToken> accessToken = authorization.getToken(OAuth2AccessToken.class);
        if (accessToken != null) {
            OAuth2AccessToken oAuth2AccessToken = accessToken.getToken();
            entity.setAccessTokenValue(oAuth2AccessToken.getTokenValue());
            entity.setAccessTokenIssuedAt(oAuth2AccessToken.getIssuedAt());
            entity.setAccessTokenExpiresAt(oAuth2AccessToken.getExpiresAt());
            entity.setAccessTokenScopes(oAuth2AccessToken.getScopes());
            entity.setAccessTokenMetadata(accessToken.getMetadata());
        }
        OAuth2Authorization.Token<OAuth2RefreshToken> refreshToken = authorization.getToken(OAuth2RefreshToken.class);
        if (refreshToken != null) {
            OAuth2RefreshToken oAuth2RefreshToken = refreshToken.getToken();
            entity.setRefreshTokenValue(oAuth2RefreshToken.getTokenValue());
            entity.setRefreshTokenIssuedAt(oAuth2RefreshToken.getIssuedAt());
            entity.setRefreshTokenExpiresAt(oAuth2RefreshToken.getExpiresAt());
            entity.setRefreshTokenMetadata(refreshToken.getMetadata());
        }
        OAuth2Authorization.Token<OAuth2AuthorizationCode> authorizationCode = authorization.getToken(OAuth2AuthorizationCode.class);
        if (authorizationCode != null) {
            entity.setAuthorizationCodeValue(authorizationCode.getToken().getTokenValue());
            entity.setAuthorizationCodeIssuedAt(authorizationCode.getToken().getIssuedAt());
            entity.setAuthorizationCodeExpiresAt(authorizationCode.getToken().getExpiresAt());
            entity.setAuthorizationCodeMetadata(authorizationCode.getMetadata());
        }

        OAuth2Authorization.Token<OidcIdToken> oidcIdToken = authorization.getToken(OidcIdToken.class);
        if (oidcIdToken != null) {
            OidcIdToken idToken = oidcIdToken.getToken();
            entity.setOidcIdTokenValue(idToken.getTokenValue());
            entity.setOidcIdTokenIssuedAt(idToken.getIssuedAt());
            entity.setOidcIdTokenExpiresAt(idToken.getExpiresAt());
            entity.setOidcIdTokenClaims(oidcIdToken.getClaims());
            entity.setOidcIdTokenMetadata(oidcIdToken.getMetadata());
        }
        OAuth2Authorization.Token<OAuth2UserCode> userCodeToken = authorization.getToken(OAuth2UserCode.class);
        if (userCodeToken != null) {
            OAuth2UserCode userCode = userCodeToken.getToken();
            entity.setUserCodeValue(userCode.getTokenValue());
            entity.setUserCodeIssuedAt(userCode.getIssuedAt());
            entity.setUserCodeExpiresAt(userCode.getExpiresAt());
            entity.setUserCodeMetadata(userCodeToken.getMetadata());
        }
        var deviceCodeToken = authorization.getToken(OAuth2DeviceCode.class);
        if (deviceCodeToken != null) {
            OAuth2DeviceCode deviceCode = deviceCodeToken.getToken();
            entity.setDeviceCodeValue(deviceCode.getTokenValue());
            entity.setDeviceCodeIssuedAt(deviceCode.getIssuedAt());
            entity.setDeviceCodeExpiresAt(deviceCode.getExpiresAt());
            entity.setDeviceCodeMetadata(deviceCodeToken.getMetadata());
        }
        return entity;
    }

    private OAuth2Authorization toObject(AuthorizationEntity entity) {
        RegisteredClient registeredClient = registeredClientRepository.findById(entity.getRegisteredClientId());
        if (registeredClient == null) {
            throw new DataRetrievalFailureException(
                    "The RegisteredClient with id '" +
                            entity.getRegisteredClientId() +
                            "' was not found in the RegisteredClientRepository.");
        }
        var builder = OAuth2Authorization.withRegisteredClient(registeredClient)
                .id(entity.getAuthorizationId())
                .principalName(entity.getPrincipalName())
                .authorizationGrantType(new AuthorizationGrantType(entity.getAuthorizationGrantType()))
                .authorizedScopes(entity.getAuthorizedScopes())
                .attributes(attributes -> attributes.putAll(entity.getAttributes()));
        if (entity.getState() != null) {
            builder.attribute(OAuth2ParameterNames.STATE, entity.getState());
        }
        if (entity.getAccessTokenValue() != null) {
            builder.accessToken(new OAuth2AccessToken(
                    OAuth2AccessToken.TokenType.BEARER,
                    entity.getAccessTokenValue(),
                    entity.getAccessTokenIssuedAt(),
                    entity.getAccessTokenExpiresAt(),
                    entity.getAccessTokenScopes()
            ));
        }
        if (entity.getRefreshTokenValue() != null) {
            builder.refreshToken(new OAuth2RefreshToken(
                    entity.getRefreshTokenValue(),
                    entity.getRefreshTokenIssuedAt(),
                    entity.getRefreshTokenExpiresAt()
            ));
        }
        if (entity.getAuthorizationCodeValue() != null) {
            builder.token(
                    new OAuth2AuthorizationCode(
                            entity.getAuthorizationCodeValue(),
                            entity.getAuthorizationCodeIssuedAt(),
                            entity.getAuthorizationCodeExpiresAt()
                    ),
                    metadata -> metadata.putAll(entity.getAuthorizationCodeMetadata())
            );
        }

        if (entity.getOidcIdTokenValue() != null) {
            builder.token(
                    new OidcIdToken(
                            entity.getOidcIdTokenValue(),
                            entity.getOidcIdTokenIssuedAt(),
                            entity.getOidcIdTokenIssuedAt(),
                            entity.getOidcIdTokenClaims()
                    ),
                    metadata -> metadata.putAll(entity.getOidcIdTokenMetadata())
            );
        }

        if (entity.getUserCodeValue() != null) {
            builder.token(
                    new OAuth2UserCode(
                            entity.getUserCodeValue(),
                            entity.getUserCodeIssuedAt(),
                            entity.getUserCodeExpiresAt()
                    ),
                    metadata -> metadata.putAll(entity.getUserCodeMetadata())
            );
        }

        if (entity.getDeviceCodeValue() != null) {
            builder.token(
                    new OAuth2DeviceCode(
                            entity.getDeviceCodeValue(),
                            entity.getDeviceCodeIssuedAt(),
                            entity.getDeviceCodeExpiresAt()
                    ),
                    metadata -> metadata.putAll(entity.getDeviceCodeMetadata())
            );
        }

        return builder.build();
    }
}
