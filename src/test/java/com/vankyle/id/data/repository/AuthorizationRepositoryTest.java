package com.vankyle.id.data.repository;

import com.vankyle.id.data.entity.Authorization;
import com.vankyle.id.data.entity.OAuth2TokenData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode;

import java.time.Instant;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class AuthorizationRepositoryTest {

    @Autowired
    private AuthorizationRepository authorizationRepository;

    private Authorization createTestEntity() {
        var testEntity = new Authorization();
        testEntity.setId("test-id");
        testEntity.setRegisteredClientId("test-registered-client-id");
        testEntity.setPrincipalName("test-principal-name");
        testEntity.setState("test-state");
        testEntity.setAuthorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE);
        testEntity.setAuthorizedScopes(Set.of("test-authorized-scope-1", "test-authorized-scope-2"));
        Instant now = Instant.now();
        Instant later = now.plusSeconds(3600);
        var authorizationCode = new OAuth2TokenData();
        authorizationCode.setTokenValue("test-authorization-code");
        authorizationCode.setTokenType(OAuth2AuthorizationCode.class);
        authorizationCode.setIssuedAt(now);
        authorizationCode.setExpiresAt(later);
        testEntity.setAuthorizationCode(authorizationCode.getTokenValue());
        testEntity.setAuthorizationCodeIssuedAt(authorizationCode.getIssuedAt());
        testEntity.setAuthorizationCodeExpiresAt(authorizationCode.getExpiresAt());

        var accessToken = new OAuth2TokenData();
        accessToken.setTokenValue("test-access-token");
        accessToken.setTokenType(OAuth2AccessToken.class);
        accessToken.setIssuedAt(Instant.now());
        accessToken.setExpiresAt(Instant.now().plusSeconds(3600));
        testEntity.setAccessToken(accessToken.getTokenValue());
        testEntity.setAccessTokenIssuedAt(accessToken.getIssuedAt());
        testEntity.setAccessTokenExpiresAt(accessToken.getExpiresAt());

        var refreshToken = new OAuth2TokenData();
        refreshToken.setTokenValue("test-refresh-token");
        refreshToken.setTokenType(OAuth2RefreshToken.class);
        refreshToken.setIssuedAt(Instant.now());
        refreshToken.setExpiresAt(Instant.now().plusSeconds(3600));
        testEntity.setRefreshToken(refreshToken.getTokenValue());
        testEntity.setRefreshTokenIssuedAt(refreshToken.getIssuedAt());
        testEntity.setRefreshTokenExpiresAt(refreshToken.getExpiresAt());


        var idToken = new OAuth2TokenData();
        idToken.setTokenValue("test-id-token");
        idToken.setTokenType(OidcIdToken.class);
        idToken.setIssuedAt(Instant.now());
        idToken.setExpiresAt(Instant.now().plusSeconds(3600));
        testEntity.setIdToken(idToken.getTokenValue());
        testEntity.setIdTokenIssuedAt(idToken.getIssuedAt());
        testEntity.setIdTokenExpiresAt(idToken.getExpiresAt());

        var tokens = Set.of(accessToken, refreshToken, idToken);
        testEntity.setTokens(tokens);


        return testEntity;
    }

    @Test
    void findByState() {
        var testEntity = createTestEntity();
        authorizationRepository.save(testEntity);
        var result = authorizationRepository.findByState("test-state");
        assertTrue(result.isPresent());
        assertEquals(testEntity, result.get());
    }

    @Test
    void findByAuthorizationCode() {
        var testEntity = createTestEntity();
        authorizationRepository.save(testEntity);
        var result = authorizationRepository.findByAuthorizationCode("test-authorization-code");
        assertTrue(result.isPresent());
        assertEquals(testEntity, result.get());
    }

    @Test
    void findByAccessToken() {
        var testEntity = createTestEntity();
        authorizationRepository.save(testEntity);
        var result = authorizationRepository.findByAccessToken("test-access-token");
        assertTrue(result.isPresent());
        assertEquals(testEntity, result.get());
    }

    @Test
    void findByRefreshToken() {
        var testEntity = createTestEntity();
        authorizationRepository.save(testEntity);
        var result = authorizationRepository.findByRefreshToken("test-refresh-token");
        assertTrue(result.isPresent());
        assertEquals(testEntity, result.get());
    }

    @Test
    void testAllCorrectTokenWhenFindAuthorizationByAuthorizationCodeOrAccessTokenOrRefreshToken() {
        var testEntity = createTestEntity();
        authorizationRepository.save(testEntity);
        var result = authorizationRepository.findAuthorizationByAuthorizationCodeOrAccessTokenOrRefreshToken(
                "test-authorization-code",
                "test-access-token",
                "test-refresh-token"
        );
        assertTrue(result.isPresent());
        assertEquals(testEntity, result.get());
    }

    @Test
    void testAuthorizationCodeCorrectTokenWhenFindAuthorizationByAuthorizationCodeOrAccessTokenOrRefreshToken() {
        var testEntity = createTestEntity();
        authorizationRepository.save(testEntity);
        var result = authorizationRepository.findAuthorizationByAuthorizationCodeOrAccessTokenOrRefreshToken(
                "test-authorization-code",
                "test-access-token-error",
                "test-refresh-token-error"
        );
        assertTrue(result.isPresent());
        assertEquals(testEntity, result.get());
    }

    @Test
    void testAccessTokenCorrectTokenWhenFindAuthorizationByAuthorizationCodeOrAccessTokenOrRefreshToken() {
        var testEntity = createTestEntity();
        authorizationRepository.save(testEntity);
        var result = authorizationRepository.findAuthorizationByAuthorizationCodeOrAccessTokenOrRefreshToken(
                "test-authorization-code-error",
                "test-access-token",
                "test-refresh-token-error"
        );
        assertTrue(result.isPresent());
        assertEquals(testEntity, result.get());
    }

    @Test
    void testRefreshTokenCorrectTokenWhenFindAuthorizationByAuthorizationCodeOrAccessTokenOrRefreshToken() {
        var testEntity = createTestEntity();
        authorizationRepository.save(testEntity);
        var result = authorizationRepository.findAuthorizationByAuthorizationCodeOrAccessTokenOrRefreshToken(
                "test-authorization-code-error",
                "test-access-token-error",
                "test-refresh-token"
        );
        assertTrue(result.isPresent());
        assertEquals(testEntity, result.get());
    }
}