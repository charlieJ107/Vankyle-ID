package com.vankyle.id.services.oauth2;

import static org.junit.jupiter.api.Assertions.*;


import com.vankyle.id.data.entities.AuthorizationConsentEntity;
import com.vankyle.id.data.repositories.AuthorizationConsentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.*;

class JpaAuthorizationConsentServiceTest {

    @Mock
    private AuthorizationConsentRepository authorizationConsentRepository;

    @Mock
    private RegisteredClientRepository registeredClientRepository;

    @InjectMocks
    private JpaAuthorizationConsentService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        AuthorizationConsentEntity entity = new AuthorizationConsentEntity();
        entity.setRegisteredClientId("client123");
        entity.setPrincipalName("user123");
        entity.setAuthorities(Set.of(new SimpleGrantedAuthority("ROLE_USER")));
        RegisteredClient client = mock(RegisteredClient.class);
        when(authorizationConsentRepository.findByRegisteredClientIdAndPrincipalName("client123", "user123")).thenReturn(Optional.of(entity));
        when(registeredClientRepository.findByClientId("client123")).thenReturn(client);
    }

    @Test
    void saveAuthorizationConsent_whenNew_savesConsent() {
        OAuth2AuthorizationConsent consent = OAuth2AuthorizationConsent
                .withId("client123", "user123")
                .authority(new SimpleGrantedAuthority("ROLE_USER"))
                .build();
        when(authorizationConsentRepository.findByRegisteredClientIdAndPrincipalName("client123", "user123")).thenReturn(Optional.empty());

        service.save(consent);

        verify(authorizationConsentRepository).save(any(AuthorizationConsentEntity.class));
    }

    @Test
    void saveAuthorizationConsent_whenExists_updatesConsent() {
        AuthorizationConsentEntity entity = new AuthorizationConsentEntity();
        entity.setRegisteredClientId("client123");
        entity.setPrincipalName("user123");
        OAuth2AuthorizationConsent consent = OAuth2AuthorizationConsent
                .withId("client123", "user123")
                .authority(new SimpleGrantedAuthority("ROLE_USER"))
                .build();
        when(authorizationConsentRepository.findByRegisteredClientIdAndPrincipalName("client123", "user123")).thenReturn(Optional.of(entity));

        service.save(consent);

        verify(authorizationConsentRepository).save(entity);
    }

    @Test
    void removeAuthorizationConsent_whenExists_removesConsent() {
        AuthorizationConsentEntity entity = new AuthorizationConsentEntity();
        entity.setRegisteredClientId("client123");
        entity.setPrincipalName("user123");
        entity.setAuthorities(Set.of(new SimpleGrantedAuthority("ROLE_USER")));
        when(authorizationConsentRepository.findByRegisteredClientIdAndPrincipalName("client123", "user123")).thenReturn(Optional.of(entity));

        service.remove(OAuth2AuthorizationConsent
                .withId("client123", "user123")
                .authority(new SimpleGrantedAuthority("ROLE_USER"))
                .build()
        );

        verify(authorizationConsentRepository).delete(entity);
    }

    @Test
    void findById_whenExists_returnsConsent() {
        OAuth2AuthorizationConsent result = service.findById("client123", "user123");

        assertNotNull(result);
    }

    @Test
    void findById_whenRegisteredClientNotFound_throwsDataRetrievalFailureException() {
        AuthorizationConsentEntity entity = new AuthorizationConsentEntity();
        entity.setRegisteredClientId("clientNotFound");
        entity.setPrincipalName("user123");
        when(authorizationConsentRepository.findByRegisteredClientIdAndPrincipalName("clientNotFound", "user123")).thenReturn(Optional.of(entity));
        when(registeredClientRepository.findByClientId("clientNotFound")).thenReturn(null);

        assertThrows(DataRetrievalFailureException.class, () -> service.findById("clientNotFound", "user123"));
    }

}