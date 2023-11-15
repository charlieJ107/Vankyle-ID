package com.vankyle.id.service.oauth;

import com.vankyle.id.data.repository.AuthorizationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")
class JpaOAuth2AuthorizationServiceTest {

    @Autowired
    private AuthorizationRepository authorizationRepository;
    @Autowired
    private RegisteredClientRepository registeredClientRepository;

    @BeforeEach
    void beforeEach() {
        var client = registeredClientRepository.findByClientId("test-client-id");
        if (client==null){
            registeredClientRepository.save(createTestRegisteredClient());
        }
        if (this.authorizationService == null) {
            this.authorizationService = new JpaOAuth2AuthorizationService(
                    authorizationRepository,
                    registeredClientRepository
            );
        }
    }
    @AfterEach
    void afterEach() {
        this.authorizationRepository.deleteAll();

    }

    private JpaOAuth2AuthorizationService authorizationService;

    private RegisteredClient createTestRegisteredClient(){
        return RegisteredClient.withId("test-registered-client-id")
                .clientId("test-client-id")
                .clientSecret("test-client-secret")
                .clientName("test-client-name")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .redirectUri("http://localhost:8080/authorized")
                .scope("test-scope-1")
                .scope("test-scope-2")
                .build();
    }

    private OAuth2Authorization createTestAuthorization() {
        var registerClient = registeredClientRepository.findByClientId("test-client-id");
        assertNotNull(registerClient);
        var builder = OAuth2Authorization
                .withRegisteredClient(registerClient)
                .principalName("test-principal-name")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .attribute("test-attribute-key", "test-attribute-value")
                .attribute("test-attribute-key-2", "test-attribute-value-2");

        var now = Instant.now();
        var later = now.plusSeconds(3600);
        var authorizationCode = new OAuth2AuthorizationCode("test-authorization-code", now, later);
        builder.token(authorizationCode);

        var accessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, "test-access-token", now, later);
        builder.accessToken(accessToken);

        var refreshToken = new OAuth2RefreshToken("test-refresh-token", now, later);
        builder.refreshToken(refreshToken);

        var oidcIdToken = new OidcIdToken(
                "test-id-token",
                now,
                later,
                Map.of(
                        "test-claim-key", "test-claim-value",
                        "test-claim-key-2", "test-claim-value-2"
                )
        );
        builder.token(oidcIdToken);


        return builder.build();
    }

    @Test
    void testSaveFindByIdRemove() {
        var testAuthorization = createTestAuthorization();
        assertNotNull(testAuthorization);
        assertNotNull(testAuthorization.getAccessToken());
        assertNotNull(testAuthorization.getRefreshToken());
        assertNotNull(testAuthorization.getToken(OidcIdToken.class));
        authorizationService.save(testAuthorization);
        var savedAuthorization = authorizationService.findById(
                testAuthorization.getId()
        );
        assertNotNull(savedAuthorization);
        assertEquals(testAuthorization.getId(), savedAuthorization.getId());
        assertEquals(testAuthorization.getRegisteredClientId(), savedAuthorization.getRegisteredClientId());
        assertEquals(testAuthorization.getPrincipalName(), savedAuthorization.getPrincipalName());
        assertEquals(testAuthorization.getAuthorizationGrantType(), savedAuthorization.getAuthorizationGrantType());
        assertEquals(testAuthorization.getAttributes(), savedAuthorization.getAttributes());
        assertNotNull(savedAuthorization.getAccessToken());
        assertEquals(testAuthorization.getAccessToken().getToken().getTokenValue(), savedAuthorization.getAccessToken().getToken().getTokenValue());
        assertNotNull(savedAuthorization.getRefreshToken());
        assertEquals(testAuthorization.getRefreshToken().getToken().getIssuedAt(), savedAuthorization.getRefreshToken().getToken().getIssuedAt());
        assertNotNull(savedAuthorization.getToken(OidcIdToken.class));
        assertEquals(testAuthorization.getToken(OidcIdToken.class).getToken().getExpiresAt(), savedAuthorization.getToken(OidcIdToken.class).getToken().getExpiresAt());

        authorizationService.remove(savedAuthorization);
        var removedAuthorization = authorizationService.findById(savedAuthorization.getId());
        assertNull(removedAuthorization);

    }

    @Test
    void testSaveFindByAccessTokenRemove() {
        var testAuthorization = createTestAuthorization();
        assertNotNull(testAuthorization);
        assertNotNull(testAuthorization.getAccessToken());
        assertNotNull(testAuthorization.getRefreshToken());
        assertNotNull(testAuthorization.getToken(OidcIdToken.class));
        authorizationService.save(testAuthorization);
        var savedAuthorization = authorizationService.findByToken(testAuthorization.getAccessToken().getToken().getTokenValue(), OAuth2TokenType.ACCESS_TOKEN);
        assertNotNull(savedAuthorization);
        assertEquals(testAuthorization.getId(), savedAuthorization.getId());
        assertEquals(testAuthorization.getRegisteredClientId(), savedAuthorization.getRegisteredClientId());
        assertEquals(testAuthorization.getPrincipalName(), savedAuthorization.getPrincipalName());
        assertEquals(testAuthorization.getAuthorizationGrantType(), savedAuthorization.getAuthorizationGrantType());
        assertEquals(testAuthorization.getAttributes(), savedAuthorization.getAttributes());
        assertNotNull(savedAuthorization.getAccessToken());
        assertEquals(testAuthorization.getAccessToken().getToken().getTokenValue(), savedAuthorization.getAccessToken().getToken().getTokenValue());
        assertNotNull(savedAuthorization.getRefreshToken());
        assertEquals(testAuthorization.getRefreshToken().getToken().getIssuedAt(), savedAuthorization.getRefreshToken().getToken().getIssuedAt());
        assertNotNull(savedAuthorization.getToken(OidcIdToken.class));
        assertEquals(testAuthorization.getToken(OidcIdToken.class).getToken().getExpiresAt(), savedAuthorization.getToken(OidcIdToken.class).getToken().getExpiresAt());

        authorizationService.remove(savedAuthorization);
        var removedAuthorization = authorizationService.findById(savedAuthorization.getId());
        assertNull(removedAuthorization);

    }

    @Test
    void testSaveFindByRefreshTokenRemove() {
        var testAuthorization = createTestAuthorization();
        assertNotNull(testAuthorization);
        assertNotNull(testAuthorization.getAccessToken());
        assertNotNull(testAuthorization.getRefreshToken());
        assertNotNull(testAuthorization.getToken(OidcIdToken.class));
        authorizationService.save(testAuthorization);
        var savedAuthorization = authorizationService.findByToken(testAuthorization.getRefreshToken().getToken().getTokenValue(), OAuth2TokenType.REFRESH_TOKEN);
        assertNotNull(savedAuthorization);
        assertEquals(testAuthorization.getId(), savedAuthorization.getId());
        assertEquals(testAuthorization.getRegisteredClientId(), savedAuthorization.getRegisteredClientId());
        assertEquals(testAuthorization.getPrincipalName(), savedAuthorization.getPrincipalName());
        assertEquals(testAuthorization.getAuthorizationGrantType(), savedAuthorization.getAuthorizationGrantType());
        assertEquals(testAuthorization.getAttributes(), savedAuthorization.getAttributes());
        assertNotNull(savedAuthorization.getAccessToken());
        assertEquals(testAuthorization.getAccessToken().getToken().getTokenValue(), savedAuthorization.getAccessToken().getToken().getTokenValue());
        assertNotNull(savedAuthorization.getRefreshToken());
        assertEquals(testAuthorization.getRefreshToken().getToken().getIssuedAt(), savedAuthorization.getRefreshToken().getToken().getIssuedAt());
        assertNotNull(savedAuthorization.getToken(OidcIdToken.class));
        assertEquals(testAuthorization.getToken(OidcIdToken.class).getToken().getExpiresAt(), savedAuthorization.getToken(OidcIdToken.class).getToken().getExpiresAt());

        authorizationService.remove(savedAuthorization);
        var removedAuthorization = authorizationService.findById(savedAuthorization.getId());
        assertNull(removedAuthorization);
    }

    @Test
    void testSaveFindByAuthorizationCodeRemove() {
        var testAuthorization = createTestAuthorization();
        assertNotNull(testAuthorization);
        assertNotNull(testAuthorization.getAccessToken());
        assertNotNull(testAuthorization.getRefreshToken());
        assertNotNull(testAuthorization.getToken(OidcIdToken.class));
        authorizationService.save(testAuthorization);
        var savedAuthorization = authorizationService.findByToken(
                testAuthorization
                        .getToken(OAuth2AuthorizationCode.class)
                        .getToken()
                        .getTokenValue(),
                new OAuth2TokenType(OAuth2ParameterNames.CODE
                )
        );
        assertNotNull(savedAuthorization);
        assertEquals(testAuthorization.getId(), savedAuthorization.getId());
        assertEquals(testAuthorization.getRegisteredClientId(), savedAuthorization.getRegisteredClientId());
        assertEquals(testAuthorization.getPrincipalName(), savedAuthorization.getPrincipalName());
        assertEquals(testAuthorization.getAuthorizationGrantType(), savedAuthorization.getAuthorizationGrantType());
        assertEquals(testAuthorization.getAttributes(), savedAuthorization.getAttributes());
        assertNotNull(savedAuthorization.getAccessToken());
        assertEquals(testAuthorization.getAccessToken().getToken().getTokenValue(), savedAuthorization.getAccessToken().getToken().getTokenValue());
        assertNotNull(savedAuthorization.getRefreshToken());
        assertEquals(testAuthorization.getRefreshToken().getToken().getIssuedAt(), savedAuthorization.getRefreshToken().getToken().getIssuedAt());
        assertNotNull(savedAuthorization.getToken(OidcIdToken.class));
        assertEquals(testAuthorization.getToken(OidcIdToken.class).getToken().getExpiresAt(), savedAuthorization.getToken(OidcIdToken.class).getToken().getExpiresAt());

        authorizationService.remove(savedAuthorization);
        var removedAuthorization = authorizationService.findById(savedAuthorization.getId());
        assertNull(removedAuthorization);
    }


}