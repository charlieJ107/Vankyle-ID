package com.vankyle.id.services.oauth2;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.vankyle.id.data.entities.ClientEntity;
import com.vankyle.id.data.repositories.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

class JpaRegisteredClientRepositoryTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private JpaRegisteredClientRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ClientEntity clientEntity = new ClientEntity();
        clientEntity.setRegisteredClientId("id");
        clientEntity.setClientId("clientId");
        clientEntity.setAuthorizationGrantTypes(Set.of(AuthorizationGrantType.AUTHORIZATION_CODE.getValue()));
        clientEntity.setClientAuthenticationMethods(Set.of(ClientAuthenticationMethod.CLIENT_SECRET_BASIC.getValue()));
        clientEntity.setRedirectUris(Set.of("http://localhost/callback"));
        clientEntity.setScopes(Set.of("read"));
        clientEntity.setClientSecret("secret");
        clientEntity.setClientName("clientName");
        clientEntity.setClientSettings(Map.of("key", "value"));
        clientEntity.setTokenSettings(Map.of("key", "value"));
        when(clientRepository.findByRegisteredClientId("id")).thenReturn(Optional.of(clientEntity));
        when(clientRepository.findByClientId("clientId")).thenReturn(Optional.of(clientEntity));
    }

    @Test
    void saveNewRegisteredClient_savesSuccessfully() {
        RegisteredClient registeredClient = createRegisteredClient();
        when(clientRepository.findByRegisteredClientId(anyString())).thenReturn(Optional.empty());

        repository.save(registeredClient);

        verify(clientRepository).save(any(ClientEntity.class));
    }

    @Test
    void saveExistingRegisteredClient_updatesSuccessfully() {
        RegisteredClient registeredClient = createRegisteredClient();
        ClientEntity existingEntity = new ClientEntity();
        existingEntity.setRegisteredClientId(registeredClient.getId());
        when(clientRepository.findByRegisteredClientId(registeredClient.getId())).thenReturn(Optional.of(existingEntity));

        repository.save(registeredClient);

        verify(clientRepository).save(existingEntity);
    }

    @Test
    void findById_whenExists_returnsRegisteredClient() {

        RegisteredClient result = repository.findById("id");

        assertNotNull(result);
    }

    @Test
    void findById_whenNotExists_returnsNull() {
        when(clientRepository.findByRegisteredClientId("unknownId")).thenReturn(Optional.empty());

        RegisteredClient result = repository.findById("unknownId");

        assertNull(result);
    }

    @Test
    void findByClientId_whenExists_returnsRegisteredClient() {

        RegisteredClient result = repository.findByClientId("clientId");

        assertNotNull(result);
    }

    @Test
    void findByClientId_whenNotExists_returnsNull() {
        when(clientRepository.findByClientId("unknownClient")).thenReturn(Optional.empty());

        RegisteredClient result = repository.findByClientId("unknownClient");

        assertNull(result);
    }

    private RegisteredClient createRegisteredClient() {
        return RegisteredClient.withId("clientId")
                .clientId("client")
                .clientSecret("secret")
                .clientAuthenticationMethods(authMethods -> authMethods.add(new ClientAuthenticationMethod("client_auth_method")))
                .authorizationGrantTypes(grantTypes -> grantTypes.add(AuthorizationGrantType.AUTHORIZATION_CODE))
                .redirectUris(redirectUris -> redirectUris.add("http://localhost/callback"))
                .scopes(scopes -> scopes.add("read"))
                .build();
    }
}