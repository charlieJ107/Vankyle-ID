package com.vankyle.id.service.oauth;

import com.vankyle.id.data.entity.Authorization;
import com.vankyle.id.data.entity.OAuth2TokenData;
import com.vankyle.id.data.repository.AuthorizationRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.security.oauth2.core.AbstractOAuth2Token;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.util.Assert;

import java.time.Instant;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public class JpaOAuth2AuthorizationService implements OAuth2AuthorizationService {
    private final AuthorizationRepository authorizationRepository;
    private final RegisteredClientRepository registeredClientRepository;
    private static final Log logger = LogFactory.getLog(JpaOAuth2AuthorizationService.class);

    public JpaOAuth2AuthorizationService(AuthorizationRepository authorizationRepository, RegisteredClientRepository registeredClientRepository) {
        this.authorizationRepository = authorizationRepository;
        this.registeredClientRepository = registeredClientRepository;
    }

    @Override
    public void save(OAuth2Authorization authorization) {
        logger.debug("Saving authorization");
        var authorizationEntity = toAuthorizationEntity(authorization);
        authorizationRepository.save(authorizationEntity);
    }

    @Override
    public void remove(OAuth2Authorization authorization) {
        logger.debug("Removing authorization");
        authorizationRepository.deleteById(authorization.getId());
    }

    @Override
    public OAuth2Authorization findById(String id) {
        logger.debug("Finding authorization by id");
        var authorizationEntity = authorizationRepository.findById(id);
        return authorizationEntity.map(this::toOAuth2Authorization).orElse(null);
    }

    @Override
    public OAuth2Authorization findByToken(String token, OAuth2TokenType tokenType) {
        Assert.hasText(token, "token cannot be empty");

        Optional<Authorization> result;
        if (tokenType == null) {
            result = this.authorizationRepository.findAuthorizationByAuthorizationCodeOrAccessTokenOrRefreshToken(
                    token, token, token);
        } else if (OAuth2ParameterNames.STATE.equals(tokenType.getValue())) {
            result = this.authorizationRepository.findByState(token);
        } else if (OAuth2ParameterNames.CODE.equals(tokenType.getValue())) {
            result = this.authorizationRepository.findByAuthorizationCode(token);
        } else if (OAuth2ParameterNames.ACCESS_TOKEN.equals(tokenType.getValue())) {
            result = this.authorizationRepository.findByAccessToken(token);
        } else if (OAuth2ParameterNames.REFRESH_TOKEN.equals(tokenType.getValue())) {
            result = this.authorizationRepository.findByRefreshToken(token);
        } else {
            // TODO: Support abstract token
            throw new IllegalArgumentException("Unsupported token type: " + tokenType.getValue());
        }

        return result.map(this::toOAuth2Authorization).orElse(null);
    }

    private Authorization toAuthorizationEntity(OAuth2Authorization authorization) {
        var authorizationEntity = new Authorization();
        authorizationEntity.setId(authorization.getId());
        authorizationEntity.setRegisteredClientId(authorization.getRegisteredClientId());
        authorizationEntity.setPrincipalName(authorization.getPrincipalName());
        authorizationEntity.setAttributes(authorization.getAttributes());
        authorizationEntity.setState(authorization.getAttribute(OAuth2ParameterNames.STATE));
        authorizationEntity.setAuthorizationGrantType(authorization.getAuthorizationGrantType());
        authorizationEntity.setAuthorizedScopes(authorization.getAuthorizedScopes());
        Set<OAuth2TokenData> tokens = new HashSet<>();
        logger.debug("Generating authorization code");
        OAuth2Authorization.Token<OAuth2AuthorizationCode> authorizationCode =
                authorization.getToken(OAuth2AuthorizationCode.class);
        var authorizationCodeTokenData = setTokenValues(
                authorizationCode,
                authorizationEntity::setAuthorizationCode,
                authorizationEntity::setAuthorizationCodeIssuedAt,
                authorizationEntity::setAuthorizationCodeExpiresAt,
                authorizationEntity::setAuthorizationCodeMetadata
        );
        if (authorizationCodeTokenData != null) {
            tokens.add(authorizationCodeTokenData);
        }
        OAuth2Authorization.Token<OAuth2AccessToken> accessToken =
                authorization.getToken(OAuth2AccessToken.class);
        var accessTokenData = setTokenValues(
                accessToken,
                authorizationEntity::setAccessToken,
                authorizationEntity::setAccessTokenIssuedAt,
                authorizationEntity::setAccessTokenExpiresAt,
                authorizationEntity::setAccessTokenMetadata
        );
        if (accessToken != null) {
            if (accessToken.getToken().getScopes() != null) {
                authorizationEntity.setAuthorizedScopes(accessToken.getToken().getScopes());
            }
            tokens.add(accessTokenData);
        }

        OAuth2Authorization.Token<OAuth2RefreshToken> refreshToken =
                authorization.getToken(OAuth2RefreshToken.class);
        var refreshTokenData = setTokenValues(
                refreshToken,
                authorizationEntity::setRefreshToken,
                authorizationEntity::setRefreshTokenIssuedAt,
                authorizationEntity::setRefreshTokenExpiresAt,
                authorizationEntity::setRefreshTokenMetadata
        );
        if (refreshTokenData != null) {
            tokens.add(refreshTokenData);
        }

        OAuth2Authorization.Token<OidcIdToken> oidcIdToken =
                authorization.getToken(OidcIdToken.class);
        OAuth2TokenData idTokenData = setTokenValues(
                oidcIdToken,
                authorizationEntity::setIdToken,
                authorizationEntity::setIdTokenIssuedAt,
                authorizationEntity::setIdTokenExpiresAt,
                authorizationEntity::setIdTokenMetadata
        );
        if (oidcIdToken != null) {
            authorizationEntity.setIdTokenClaims(oidcIdToken.getClaims());
            tokens.add(idTokenData);
        }
        authorizationEntity.setTokens(tokens);
        return authorizationEntity;
    }

    private OAuth2Authorization toOAuth2Authorization(Authorization authorizationEntity) {
        RegisteredClient registeredClient = registeredClientRepository
                .findById(authorizationEntity.getRegisteredClientId());
        if (registeredClient == null) {
            throw new DataRetrievalFailureException(
                    "The RegisteredClient with id '" +
                            authorizationEntity.getRegisteredClientId() +
                            "' was not found in the RegisteredClientRepository.");
        }
        var builder = OAuth2Authorization.withRegisteredClient(registeredClient)
                .id(authorizationEntity.getId())
                .principalName(authorizationEntity.getPrincipalName())
                .attributes(attributes ->
                        attributes.putAll(authorizationEntity.getAttributes()))
                .authorizationGrantType(authorizationEntity.getAuthorizationGrantType())
                .authorizedScopes(authorizationEntity.getAuthorizedScopes());
        if (authorizationEntity.getState() != null) {
            builder.attribute(OAuth2ParameterNames.STATE, authorizationEntity.getState());
        }
        if (authorizationEntity.getAuthorizationCode() != null) {
            OAuth2AuthorizationCode authorizationCode = new OAuth2AuthorizationCode(
                    authorizationEntity.getAuthorizationCode(),
                    authorizationEntity.getAuthorizationCodeIssuedAt(),
                    authorizationEntity.getAuthorizationCodeExpiresAt());
            builder.token(authorizationCode, metadata -> metadata.putAll(authorizationEntity.getAuthorizationCodeMetadata()));
        }

        if (authorizationEntity.getAccessToken() != null) {
            OAuth2AccessToken accessToken = new OAuth2AccessToken(
                    OAuth2AccessToken.TokenType.BEARER,
                    authorizationEntity.getAccessToken(),
                    authorizationEntity.getAccessTokenIssuedAt(),
                    authorizationEntity.getAccessTokenExpiresAt(),
                    authorizationEntity.getAuthorizedScopes());
            builder.token(accessToken, metadata -> metadata.putAll(authorizationEntity.getAccessTokenMetadata()));
        }

        if (authorizationEntity.getRefreshToken() != null) {
            OAuth2RefreshToken refreshToken = new OAuth2RefreshToken(
                    authorizationEntity.getRefreshToken(),
                    authorizationEntity.getRefreshTokenIssuedAt(),
                    authorizationEntity.getRefreshTokenExpiresAt());
            builder.token(refreshToken, metadata -> metadata.putAll(authorizationEntity.getRefreshTokenMetadata()));
        }

        if (authorizationEntity.getIdToken() != null) {
            OidcIdToken idToken = new OidcIdToken(
                    authorizationEntity.getIdToken(),
                    authorizationEntity.getIdTokenIssuedAt(),
                    authorizationEntity.getIdTokenExpiresAt(),
                    authorizationEntity.getIdTokenClaims());
            builder.token(idToken, metadata -> metadata.putAll(authorizationEntity.getIdTokenMetadata()));
        }
        return builder.build();
    }

    private OAuth2TokenData setTokenValues(
            OAuth2Authorization.Token<?> token,
            Consumer<String> tokenValueConsumer,
            Consumer<Instant> issuedAtConsumer,
            Consumer<Instant> expiresAtConsumer,
            Consumer<Map<String, Object>> metadataConsumer) {
        if (token != null) {
            OAuth2Token oAuth2Token = token.getToken();
            tokenValueConsumer.accept(oAuth2Token.getTokenValue());
            issuedAtConsumer.accept(oAuth2Token.getIssuedAt());
            expiresAtConsumer.accept(oAuth2Token.getExpiresAt());
            metadataConsumer.accept(token.getMetadata());
            var tokenData = new OAuth2TokenData();
            tokenData.setTokenType(oAuth2Token.getClass());
            tokenData.setTokenValue(oAuth2Token.getTokenValue());
            tokenData.setExpiresAt(oAuth2Token.getExpiresAt());
            tokenData.setIssuedAt(oAuth2Token.getIssuedAt());
            return tokenData;
        }
        return null;
    }
}
