package com.vankyle.id.data.repositories;

import static org.junit.jupiter.api.Assertions.*;

import com.vankyle.id.data.entities.AuthorizationConsentEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.mockito.Mockito.*;

@DataJpaTest
public class AuthorizationConsentRepositoryTest {

    @Mock
    private AuthorizationConsentRepository authorizationConsentRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findByRegisteredClientIdAndPrincipalName_whenExists_returnsConsentEntity() {
        String registeredClientId = "client123";
        String principalName = "user123";
        AuthorizationConsentEntity consentEntity = new AuthorizationConsentEntity();
        when(authorizationConsentRepository.findByRegisteredClientIdAndPrincipalName(registeredClientId, principalName)).thenReturn(Optional.of(consentEntity));

        Optional<AuthorizationConsentEntity> result = authorizationConsentRepository.findByRegisteredClientIdAndPrincipalName(registeredClientId, principalName);

        assertTrue(result.isPresent());
    }

    @Test
    void findByRegisteredClientIdAndPrincipalName_whenNotExists_returnsEmpty() {
        String registeredClientId = "clientNotFound";
        String principalName = "userNotFound";
        when(authorizationConsentRepository.findByRegisteredClientIdAndPrincipalName(registeredClientId, principalName)).thenReturn(Optional.empty());

        Optional<AuthorizationConsentEntity> result = authorizationConsentRepository.findByRegisteredClientIdAndPrincipalName(registeredClientId, principalName);

        assertFalse(result.isPresent());
    }

    @Test
    void deleteByRegisteredClientIdAndPrincipalName_whenExists_performsDeletion() {
        String registeredClientId = "clientToDelete";
        String principalName = "userToDelete";
        doNothing().when(authorizationConsentRepository).deleteByRegisteredClientIdAndPrincipalName(registeredClientId, principalName);

        authorizationConsentRepository.deleteByRegisteredClientIdAndPrincipalName(registeredClientId, principalName);

        verify(authorizationConsentRepository, times(1)).deleteByRegisteredClientIdAndPrincipalName(registeredClientId, principalName);
    }
}