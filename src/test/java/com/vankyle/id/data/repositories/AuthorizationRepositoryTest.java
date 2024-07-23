package com.vankyle.id.data.repositories;

import com.vankyle.id.data.entities.AuthorizationEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@DataJpaTest
class AuthorizationRepositoryTest {

    @Mock
    private AuthorizationRepository authorizationRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findByState_whenStateExists_returnsAuthorizationEntity() {
        String state = "testState";
        AuthorizationEntity authorizationEntity = new AuthorizationEntity();
        when(authorizationRepository.findByState(state)).thenReturn(Optional.of(authorizationEntity));

        Optional<AuthorizationEntity> result = authorizationRepository.findByState(state);

        assertTrue(result.isPresent());
    }

    @Test
    void findByAuthorizationCodeValue_whenCodeExists_returnsAuthorizationEntity() {
        String code = "authCode";
        AuthorizationEntity authorizationEntity = new AuthorizationEntity();
        when(authorizationRepository.findByAuthorizationCodeValue(code)).thenReturn(Optional.of(authorizationEntity));

        Optional<AuthorizationEntity> result = authorizationRepository.findByAuthorizationCodeValue(code);

        assertTrue(result.isPresent());
    }

    @Test
    void findByRefreshTokenValue_whenTokenExists_returnsAuthorizationEntity() {
        String refreshToken = "refreshToken";
        AuthorizationEntity authorizationEntity = new AuthorizationEntity();
        when(authorizationRepository.findByRefreshTokenValue(refreshToken)).thenReturn(Optional.of(authorizationEntity));

        Optional<AuthorizationEntity> result = authorizationRepository.findByRefreshTokenValue(refreshToken);

        assertTrue(result.isPresent());
    }

    @Test
    void findByAccessTokenValue_whenTokenExists_returnsAuthorizationEntity() {
        String accessToken = "accessToken";
        AuthorizationEntity authorizationEntity = new AuthorizationEntity();
        when(authorizationRepository.findByAccessTokenValue(accessToken)).thenReturn(Optional.of(authorizationEntity));

        Optional<AuthorizationEntity> result = authorizationRepository.findByAccessTokenValue(accessToken);

        assertTrue(result.isPresent());
    }

    @Test
    void findByOidcIdTokenValue_whenTokenExists_returnsAuthorizationEntity() {
        String oidcIdToken = "oidcIdToken";
        AuthorizationEntity authorizationEntity = new AuthorizationEntity();
        when(authorizationRepository.findByOidcIdTokenValue(oidcIdToken)).thenReturn(Optional.of(authorizationEntity));

        Optional<AuthorizationEntity> result = authorizationRepository.findByOidcIdTokenValue(oidcIdToken);

        assertTrue(result.isPresent());
    }

    @Test
    void findByUserCodeValue_whenCodeExists_returnsAuthorizationEntity() {
        String userCode = "userCode";
        AuthorizationEntity authorizationEntity = new AuthorizationEntity();
        when(authorizationRepository.findByUserCodeValue(userCode)).thenReturn(Optional.of(authorizationEntity));

        Optional<AuthorizationEntity> result = authorizationRepository.findByUserCodeValue(userCode);

        assertTrue(result.isPresent());
    }

    @Test
    void findByDeviceCodeValue_whenCodeExists_returnsAuthorizationEntity() {
        String deviceCode = "deviceCode";
        AuthorizationEntity authorizationEntity = new AuthorizationEntity();
        when(authorizationRepository.findByDeviceCodeValue(deviceCode)).thenReturn(Optional.of(authorizationEntity));

        Optional<AuthorizationEntity> result = authorizationRepository.findByDeviceCodeValue(deviceCode);

        assertTrue(result.isPresent());
    }
    @Test
    void findByToken_whenStateMatches_returnsAuthorizationEntity() {
        String token = "stateToken";
        AuthorizationEntity authorizationEntity = new AuthorizationEntity();
        when(authorizationRepository.findByToken(token)).thenReturn(Optional.of(authorizationEntity));

        Optional<AuthorizationEntity> result = authorizationRepository.findByToken(token);

        assertTrue(result.isPresent());
    }
    @Test
    void findByToken_whenAuthorizationCodeValueMatches_returnsAuthorizationEntity() {
        String token = "authCodeToken";
        AuthorizationEntity authorizationEntity = new AuthorizationEntity();
        when(authorizationRepository.findByToken(token)).thenReturn(Optional.of(authorizationEntity));

        Optional<AuthorizationEntity> result = authorizationRepository.findByToken(token);

        assertTrue(result.isPresent());
    }
    @Test
    void findByToken_whenRefreshTokenValueMatches_returnsAuthorizationEntity() {
        String token = "refreshTokenToken";
        AuthorizationEntity authorizationEntity = new AuthorizationEntity();
        when(authorizationRepository.findByToken(token)).thenReturn(Optional.of(authorizationEntity));

        Optional<AuthorizationEntity> result = authorizationRepository.findByToken(token);

        assertTrue(result.isPresent());
    }
    @Test
    void findByToken_whenAccessTokenValueMatches_returnsAuthorizationEntity() {
        String token = "accessTokenToken";
        AuthorizationEntity authorizationEntity = new AuthorizationEntity();
        when(authorizationRepository.findByToken(token)).thenReturn(Optional.of(authorizationEntity));

        Optional<AuthorizationEntity> result = authorizationRepository.findByToken(token);

        assertTrue(result.isPresent());
    }
    @Test
    void findByToken_whenOidcIdTokenValueMatches_returnsAuthorizationEntity() {
        String token = "oidcIdTokenToken";
        AuthorizationEntity authorizationEntity = new AuthorizationEntity();
        when(authorizationRepository.findByToken(token)).thenReturn(Optional.of(authorizationEntity));

        Optional<AuthorizationEntity> result = authorizationRepository.findByToken(token);

        assertTrue(result.isPresent());
    }
    @Test
    void findByToken_whenUserCodeValueMatches_returnsAuthorizationEntity() {
        String token = "userCodeToken";
        AuthorizationEntity authorizationEntity = new AuthorizationEntity();
        when(authorizationRepository.findByToken(token)).thenReturn(Optional.of(authorizationEntity));

        Optional<AuthorizationEntity> result = authorizationRepository.findByToken(token);

        assertTrue(result.isPresent());
    }
    @Test
    void findByToken_whenDeviceCodeValueMatches_returnsAuthorizationEntity() {
        String token = "deviceCodeToken";
        AuthorizationEntity authorizationEntity = new AuthorizationEntity();
        when(authorizationRepository.findByToken(token)).thenReturn(Optional.of(authorizationEntity));

        Optional<AuthorizationEntity> result = authorizationRepository.findByToken(token);

        assertTrue(result.isPresent());
    }
    @Test
    void findByToken_whenNoMatch_returnsEmpty() {
        String token = "nonExistentToken";
        when(authorizationRepository.findByToken(token)).thenReturn(Optional.empty());

        Optional<AuthorizationEntity> result = authorizationRepository.findByToken(token);

        assertFalse(result.isPresent());
    }
}