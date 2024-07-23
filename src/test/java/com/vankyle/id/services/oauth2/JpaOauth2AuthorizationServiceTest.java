package com.vankyle.id.services.oauth2;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.vankyle.id.data.entities.AuthorizationEntity;
import com.vankyle.id.data.repositories.AuthorizationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

class JpaOauth2AuthorizationServiceTest {

    @Mock
    private AuthorizationRepository authorizationRepository;

    @Mock
    private RegisteredClientRepository registeredClientRepository;

    @InjectMocks
    private JpaOauth2AuthorizationService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        var mockClient = mock(RegisteredClient.class);
        when(mockClient.getId()).thenReturn("clientId");
        when(registeredClientRepository.findById(anyString())).thenReturn(mockClient);
        AuthorizationEntity entity = new AuthorizationEntity();
        entity.setAuthorizationId("authId");
        entity.setRegisteredClientId("clientId");
        entity.setAccessTokenValue("accessToken");
        entity.setAuthorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE.getValue());
        entity.setAttributes(Map.of("attr", "value"));
        entity.setPrincipalName("user");
        when(authorizationRepository.findByAuthorizationId("authId")).thenReturn(Optional.of(entity));
        when(authorizationRepository.findByAccessTokenValue("accessToken")).thenReturn(Optional.of(entity));
    }

    @Test
    void saveAuthorization_whenNew_savesSuccessfully() {
        OAuth2Authorization authorization = createOAuth2Authorization();
        when(authorizationRepository.findByAuthorizationId(anyString())).thenReturn(Optional.empty());

        service.save(authorization);

        verify(authorizationRepository).save(any(AuthorizationEntity.class));
    }

    @Test
    void removeAuthorization_whenExists_removesSuccessfully() {
        OAuth2Authorization authorization = createOAuth2Authorization();
        when(authorizationRepository.findByAuthorizationId(anyString())).thenReturn(Optional.of(new AuthorizationEntity()));

        service.remove(authorization);

        verify(authorizationRepository).delete(any(AuthorizationEntity.class));
    }

    @Test
    void findById_whenExists_returnsAuthorization() {

        OAuth2Authorization result = service.findById("authId");

        assertNotNull(result);
    }

    @Test
    void findById_whenNotExists_returnsNull() {
        when(authorizationRepository.findByAuthorizationId("nonExistingId")).thenReturn(Optional.empty());

        OAuth2Authorization result = service.findById("nonExistingId");

        assertNull(result);
    }

    @Test
    void findByToken_whenAccessTokenExists_returnsAuthorization() {
        OAuth2Authorization result = service.findByToken("accessToken", OAuth2TokenType.ACCESS_TOKEN);
        assertNotNull(result);
    }

    @Test
    void findByToken_whenTokenNotFound_returnsNull() {
        when(authorizationRepository.findByAccessTokenValue("unknownToken")).thenReturn(Optional.empty());

        OAuth2Authorization result = service.findByToken("unknownToken", OAuth2TokenType.ACCESS_TOKEN);

        assertNull(result);
    }

    private OAuth2Authorization createOAuth2Authorization() {
        RegisteredClient registeredClient = RegisteredClient.withId("clientId")
                .clientId("client")
                .clientSecret("secret")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("http://localhost/callback")
                .scope("read")
                .build();
        return OAuth2Authorization.withRegisteredClient(registeredClient)
                .principalName("user")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .attribute("attr", "value")
                .token(new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, "tokenValue", Instant.now(), Instant.now().plusSeconds(60)))
                .build();
    }
}