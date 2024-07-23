package com.vankyle.id.services.oauth2;

import com.vankyle.id.data.entities.ClientEntity;
import com.vankyle.id.data.repositories.ClientRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.util.Assert;

import java.util.stream.Collectors;

public class JpaRegisteredClientRepository implements RegisteredClientRepository {

    private final ClientRepository clientRepository;

    public JpaRegisteredClientRepository(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public void save(RegisteredClient registeredClient) {
        Assert.notNull(registeredClient, "registeredClient cannot be null");
        // Find if there's an existing client with the same id
        ClientEntity clientEntity = clientRepository.findByRegisteredClientId(registeredClient.getId()).orElse(new ClientEntity());
        clientEntity = toEntity(registeredClient, clientEntity);
        clientRepository.save(clientEntity);
    }

    @Override
    public RegisteredClient findById(String id) {
        return clientRepository.findByRegisteredClientId(id)
                .map(this::toObject)
                .orElse(null);
    }

    @Override
    public RegisteredClient findByClientId(String clientId) {
        return clientRepository.findByClientId(clientId)
                .map(this::toObject)
                .orElse(null);
    }

    private RegisteredClient toObject(ClientEntity clientEntity) {
        return RegisteredClient.withId(clientEntity.getRegisteredClientId())
                .clientId(clientEntity.getClientId())
                .clientSecret(clientEntity.getClientSecret())
                .clientName(clientEntity.getClientName())
                .clientAuthenticationMethods(clientAuthenticationMethods ->
                        clientAuthenticationMethods.addAll(clientEntity.getClientAuthenticationMethods().stream()
                                .map(ClientAuthenticationMethod::new)
                                .collect(Collectors.toSet())
                        ))
                .authorizationGrantTypes(authorizationGrantTypes ->
                        authorizationGrantTypes.addAll(clientEntity.getAuthorizationGrantTypes().stream()
                                .map(AuthorizationGrantType::new)
                                .collect(Collectors.toSet())
                        ))
                .redirectUris(redirectUris -> redirectUris.addAll(clientEntity.getRedirectUris()))
                .scopes(scopes -> scopes.addAll(clientEntity.getScopes()))
                .clientSettings(ClientSettings.withSettings(clientEntity.getClientSettings()).build())
                .tokenSettings(TokenSettings.withSettings(clientEntity.getTokenSettings()).build())
                .build();
    }

    private ClientEntity toEntity(RegisteredClient registeredClient, ClientEntity clientEntity) {
        clientEntity.setRegisteredClientId(registeredClient.getId());
        clientEntity.setClientId(registeredClient.getClientId());
        clientEntity.setClientSecret(registeredClient.getClientSecret());
        clientEntity.setClientName(registeredClient.getClientName());
        clientEntity.setClientAuthenticationMethods(registeredClient.getClientAuthenticationMethods().stream()
                .map(ClientAuthenticationMethod::getValue)
                .collect(Collectors.toSet())
        );
        clientEntity.setAuthorizationGrantTypes(registeredClient.getAuthorizationGrantTypes().stream()
                .map(AuthorizationGrantType::getValue)
                .collect(Collectors.toSet())
        );
        clientEntity.setRedirectUris(registeredClient.getRedirectUris());
        clientEntity.setScopes(registeredClient.getScopes());
        clientEntity.setClientSettings(registeredClient.getClientSettings().getSettings());
        clientEntity.setTokenSettings(registeredClient.getTokenSettings().getSettings());
        return clientEntity;
    }

}
