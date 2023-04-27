package com.vankyle.id.service.oauth;

import com.vankyle.id.data.entity.Client;
import com.vankyle.id.data.entity.TokenSettingsData;
import com.vankyle.id.data.repository.ClientRepository;
import com.vankyle.id.data.entity.ClientSettingsData;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.util.Assert;

public class JpaRegisteredClientRepository implements RegisteredClientRepository {

    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;

    private static final Log logger = LogFactory.getLog(JpaRegisteredClientRepository.class);

    public JpaRegisteredClientRepository(ClientRepository clientRepository, PasswordEncoder passwordEncoder) {
        this.clientRepository = clientRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void save(RegisteredClient registeredClient) {
        Assert.notNull(registeredClient, "registeredClient cannot be null");
        logger.debug("Saving registered client: " + registeredClient);
        Client client = toClientEntity(registeredClient);
        clientRepository.save(client);
    }

    @Override
    public RegisteredClient findById(String id) {
        Assert.hasText(id, "id cannot be empty");
        logger.debug("Finding registered client by id: " + id);
        Client client = clientRepository.findById(id).orElse(null);
        return client == null ? null : toRegisteredClient(client);
    }

    @Override
    public RegisteredClient findByClientId(String clientId) {
        Assert.hasText(clientId, "clientId cannot be empty");
        logger.debug("Finding registered client by client id: " + clientId);
        Client client = clientRepository.findByClientId(clientId);
        return client == null ? null : toRegisteredClient(client);
    }

    private RegisteredClient toRegisteredClient(Client client) {
        return RegisteredClient.withId(client.getClientId())
                .clientId(client.getClientId())
                .clientSecret(passwordEncoder.encode(client.getClientSecret()))
                .clientAuthenticationMethods(clientAuthenticationMethods ->
                        clientAuthenticationMethods.addAll(client.getClientAuthenticationMethods()))
                .authorizationGrantTypes(authorizationGrantTypes ->
                        authorizationGrantTypes.addAll(client.getAuthorizationGrantTypes()))
                .scopes(scopes -> scopes.addAll(client.getScopes()))
                .redirectUris(redirectUris -> redirectUris.addAll(client.getRedirectUris()))
                .clientSettings(toClientSettings(client.getClientSettings()))
                .tokenSettings(toTokenSettings(client.getTokenSettings()))
                .build();
    }

    private Client toClientEntity(RegisteredClient registeredClient) {
        Client client = new Client();
        client.setClientId(registeredClient.getClientId());
        client.setClientSecret(registeredClient.getClientSecret());
        client.setClientAuthenticationMethods(registeredClient.getClientAuthenticationMethods());
        client.setAuthorizationGrantTypes(registeredClient.getAuthorizationGrantTypes());
        client.setScopes(registeredClient.getScopes());
        client.setRedirectUris(registeredClient.getRedirectUris());
        client.setClientSettings(toClientSettingsData(registeredClient.getClientSettings()));
        client.setTokenSettings(toTokenSettingsData(registeredClient.getTokenSettings()));
        return client;
    }


    private ClientSettings toClientSettings(ClientSettingsData data) {
        var builder = ClientSettings.withSettings(data.getClientSettings())
                .requireAuthorizationConsent(data.isRequireAuthorizationConsent())
                .requireProofKey(data.isRequireProofKey());
        if (data.getJwkSetUri() != null) {
            builder.jwkSetUrl(data.getJwkSetUri());
        }
        if (data.getTokenEndpointAuthenticationSigningAlgorithm() != null) {
            builder.tokenEndpointAuthenticationSigningAlgorithm(data.getTokenEndpointAuthenticationSigningAlgorithm());
        }
        return builder.build();
    }

    private TokenSettings toTokenSettings(TokenSettingsData data) {
        return TokenSettings.builder()
                .accessTokenTimeToLive(data.getAccessTokenTimeToLive())
                .refreshTokenTimeToLive(data.getRefreshTokenTimeToLive())
                .authorizationCodeTimeToLive(data.getAuthorizationCodeTimeToLive())
                .idTokenSignatureAlgorithm(SignatureAlgorithm.from(data.getIdTokenSignatureAlgorithm().getName()))
                .reuseRefreshTokens(data.isReuseRefreshTokens())
                .settings(stringObjectMap -> stringObjectMap.putAll(data.getTokenSettings()))
                .build();
    }

    private ClientSettingsData toClientSettingsData(ClientSettings settings) {
        var data = new ClientSettingsData();
        data.setClientSettings(settings.getSettings());
        data.setJwkSetUri(settings.getJwkSetUrl());
        data.setTokenEndpointAuthenticationSigningAlgorithm(settings.getTokenEndpointAuthenticationSigningAlgorithm());
        data.setRequireAuthorizationConsent(settings.isRequireAuthorizationConsent());
        data.setRequireProofKey(settings.isRequireProofKey());
        return data;
    }

    private TokenSettingsData toTokenSettingsData(TokenSettings settings) {
        var data = new TokenSettingsData();
        data.setAccessTokenTimeToLive(settings.getAccessTokenTimeToLive());
        data.setRefreshTokenTimeToLive(settings.getRefreshTokenTimeToLive());
        data.setAuthorizationCodeTimeToLive(settings.getAuthorizationCodeTimeToLive());
        data.setIdTokenSignatureAlgorithm(settings.getIdTokenSignatureAlgorithm());
        data.setReuseRefreshTokens(settings.isReuseRefreshTokens());
        data.setTokenSettings(settings.getSettings());
        return data;
    }
}
