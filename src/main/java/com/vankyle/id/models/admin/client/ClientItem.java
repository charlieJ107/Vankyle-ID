package com.vankyle.id.models.admin.client;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.util.StringUtils;

import java.io.Serial;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class ClientItem implements Serializable {
    @Serial
    private static final long serialVersionUID = -2751481581473585795L;

    public ClientItem(RegisteredClient client) {
        this.id = client.getId();
        this.clientId = client.getClientId();
        this.clientIdIssuedAt = client.getClientIdIssuedAt();
        this.clientSecret = client.getClientSecret();
        this.clientSecretExpiresAt = client.getClientSecretExpiresAt();
        this.clientName = client.getClientName();
        this.clientAuthenticationMethods = client.getClientAuthenticationMethods().stream()
                .map(ClientAuthenticationMethod::getValue).collect(Collectors.toSet());
        this.authorizationGrantTypes = client.getAuthorizationGrantTypes().stream()
                .map(AuthorizationGrantType::getValue).collect(Collectors.toSet());
        this.redirectUris = client.getRedirectUris();
        this.scopes = client.getScopes();
        this.clientSettings = new ClientSettings();
        this.clientSettings.setJwkSetUri(client.getClientSettings().getJwkSetUrl());
        this.clientSettings.setRequireProofKey(client.getClientSettings().isRequireProofKey());
        this.clientSettings.setRequireAuthorizationConsent(client.getClientSettings().isRequireAuthorizationConsent());
        this.clientSettings.setTokenEndpointAuthenticationSigningAlgorithm(
                client.getClientSettings().getTokenEndpointAuthenticationSigningAlgorithm() == null ? null :
                        client.getClientSettings().getTokenEndpointAuthenticationSigningAlgorithm().getName());
        this.tokenSettings = new TokenSettings();
        this.tokenSettings.setAuthorizationCodeTimeToLive(
                client.getTokenSettings().getAuthorizationCodeTimeToLive().toMinutes());
        this.tokenSettings.setAccessTokenTimeToLive(
                client.getTokenSettings().getAccessTokenTimeToLive().toMinutes());
        this.tokenSettings.setAccessTokenFormat(
                client.getTokenSettings().getAccessTokenFormat() == null ? null :
                        client.getTokenSettings().getAccessTokenFormat().getValue());
        this.tokenSettings.setRefreshTokenTimeToLive(
                client.getTokenSettings().getRefreshTokenTimeToLive().toMinutes());
        this.tokenSettings.setReuseRefreshTokens(
                client.getTokenSettings().isReuseRefreshTokens());
        this.tokenSettings.setIdTokenSignatureAlgorithm(
                client.getTokenSettings().getIdTokenSignatureAlgorithm() == null ? null :
                        client.getTokenSettings().getIdTokenSignatureAlgorithm().getName());
    }

    public RegisteredClient toRegisteredClient() {
        if (!StringUtils.hasText(this.id)) {
            id = this.clientId;
        }
        return RegisteredClient.withId(id)
                .clientId(clientId)
                .clientIdIssuedAt(clientIdIssuedAt)
                .clientSecret(clientSecret)
                .clientSecretExpiresAt(clientSecretExpiresAt)
                .clientName(clientName)
                .clientAuthenticationMethods(clientAuthenticationMethods ->
                        clientAuthenticationMethods.addAll(this.clientAuthenticationMethods.stream()
                                .map(ClientAuthenticationMethod::new).collect(Collectors.toSet())))
                .authorizationGrantTypes(authorizationGrantTypes ->
                        authorizationGrantTypes.addAll(this.authorizationGrantTypes.stream().map(
                                AuthorizationGrantType::new).collect(Collectors.toSet())))
                .redirectUris(redirectUris -> redirectUris.addAll(this.redirectUris))
                .scopes(scopes -> scopes.addAll(this.scopes))
                .clientSettings(this.clientSettings.toClientSettings())
                .tokenSettings(this.tokenSettings.toTokenSettings())
                .build();
    }

    private String id;
    private String clientId;
    private Instant clientIdIssuedAt;
    private String clientSecret;
    private Instant clientSecretExpiresAt;
    private String clientName;
    private Set<String> clientAuthenticationMethods;
    private Set<String> authorizationGrantTypes;
    private Set<String> redirectUris;
    private Set<String> scopes;
    private ClientSettings clientSettings;
    private TokenSettings tokenSettings;

    @Data
    public static class ClientSettings implements Serializable {
        @Serial
        private static final long serialVersionUID = 5017965212438219409L;
        private boolean isRequireProofKey;
        private boolean isRequireAuthorizationConsent;
        private String jwkSetUri;
        private String tokenEndpointAuthenticationSigningAlgorithm;

        public org.springframework.security.oauth2.server.authorization.settings.ClientSettings toClientSettings() {
            var builder = org.springframework.security.oauth2.server.authorization.settings.ClientSettings.builder()
                    .requireProofKey(isRequireProofKey)
                    .requireAuthorizationConsent(isRequireAuthorizationConsent);
            if (jwkSetUri != null && !jwkSetUri.isBlank()) {
                builder.jwkSetUrl(jwkSetUri);
            }
            if (tokenEndpointAuthenticationSigningAlgorithm != null &&
                    !tokenEndpointAuthenticationSigningAlgorithm.isBlank()) {
                builder.tokenEndpointAuthenticationSigningAlgorithm(
                        SignatureAlgorithm.from(tokenEndpointAuthenticationSigningAlgorithm));
            }
            return builder.build();
        }
    }

    @Data
    public static class TokenSettings implements Serializable {
        @Serial
        private static final long serialVersionUID = 5017965212438219409L;
        private long authorizationCodeTimeToLive;
        private long accessTokenTimeToLive;
        private String accessTokenFormat;
        private long refreshTokenTimeToLive;
        private boolean reuseRefreshTokens;
        private String idTokenSignatureAlgorithm;

        public org.springframework.security.oauth2.server.authorization.settings.TokenSettings toTokenSettings() {
            var builder = org.springframework.security.oauth2.server.authorization.settings.TokenSettings.builder()
                    .authorizationCodeTimeToLive(Duration.ofMinutes(authorizationCodeTimeToLive))
                    .accessTokenTimeToLive(Duration.ofMinutes(accessTokenTimeToLive))
                    .refreshTokenTimeToLive(Duration.ofMinutes(refreshTokenTimeToLive))
                    .reuseRefreshTokens(reuseRefreshTokens);
            if (accessTokenFormat != null) {
                builder.accessTokenFormat(new OAuth2TokenFormat(accessTokenFormat));
            }
            if (idTokenSignatureAlgorithm != null) {
                builder.idTokenSignatureAlgorithm(SignatureAlgorithm.from(idTokenSignatureAlgorithm));
            }
            return builder.build();
        }
    }
}
