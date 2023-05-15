package com.vankyle.id.config;

import com.vankyle.id.config.handlers.JsonResponseAuthenticationEntryPoint;
import com.vankyle.id.config.handlers.JsonResponseAuthorizationEndpointHandler;
import com.vankyle.id.config.jose.JsonWebKeys;
import com.vankyle.id.config.jwt.RoleBasedJwtAuthenticationConverter;
import com.vankyle.id.config.properties.ApplicationProperties;
import com.vankyle.id.data.repository.AuthorizationConsentRepository;
import com.vankyle.id.data.repository.AuthorizationRepository;
import com.vankyle.id.data.repository.ClientRepository;
import com.vankyle.id.service.oauth.JpaOAuth2AuthorizationConsentService;
import com.vankyle.id.service.oauth.JpaOAuth2AuthorizationService;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.vankyle.id.service.oauth.JpaRegisteredClientService;
import com.vankyle.id.service.oauth.RegisteredClientService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Configuration
@EnableConfigurationProperties(ApplicationProperties.class)
public class AuthorizationServerConfig {
    private static final Log logger = LogFactory.getLog(AuthorizationServerConfig.class);
    @Value("${vankyle.id.base-url}")
    private String base_url;

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
                new OAuth2AuthorizationServerConfigurer();
        authorizationServerConfigurer
                .authorizationEndpoint(authorizationEndpoint ->
                        authorizationEndpoint.consentPage("/consent")
                                .authorizationResponseHandler(new JsonResponseAuthorizationEndpointHandler())
                                .errorResponseHandler(new JsonResponseAuthorizationEndpointHandler())
                )
                .oidc(Customizer.withDefaults()); // Enable OpenID Connect 1.0
        RequestMatcher endpointsMatcher = authorizationServerConfigurer
                .getEndpointsMatcher();

        http.securityMatcher(endpointsMatcher)
                .authorizeHttpRequests(authorize ->
                        authorize.anyRequest().authenticated()
                )
                .csrf(csrf -> csrf.ignoringRequestMatchers(endpointsMatcher))
                .exceptionHandling(exceptions ->
                        exceptions.authenticationEntryPoint(new JsonResponseAuthenticationEntryPoint("/login"))
                )
                .oauth2ResourceServer(httpSecurityOAuth2ResourceServerConfigurer ->
                        httpSecurityOAuth2ResourceServerConfigurer.jwt()
                                .jwtAuthenticationConverter(new RoleBasedJwtAuthenticationConverter()))
                .apply(authorizationServerConfigurer);
        return http.build();
    }

    @Bean
    public RegisteredClientService registeredClientRepository(
            ClientRepository clientRepository,
            PasswordEncoder passwordEncoder
    ) {
        JpaRegisteredClientService registeredClientRepository = new JpaRegisteredClientService(
                clientRepository, passwordEncoder);
        logger.debug("Creating user frontend");
        RegisteredClient account_frontend = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("account")
                .clientIdIssuedAt(Instant.now())
                .clientName("Vankyle ID")
                .clientSecret("client_secret")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri(base_url + "/oidc")
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .scope(OidcScopes.EMAIL)
                .scope(OidcScopes.ADDRESS)
                .scope(OidcScopes.PHONE)
                .clientSettings(
                        ClientSettings.builder()
                                .requireAuthorizationConsent(true)
                                .build())
                .build();
        if (registeredClientRepository.findByClientId("account") == null) {
            registeredClientRepository.save(account_frontend);
        } else {
            logger.warn("Default client \"account\" has already been created, skipping to add new.");
        }

        return registeredClientRepository;
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        RSAKey rsaKey = JsonWebKeys.generateRsa();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    }

    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().build();
    }

    @Bean
    public OAuth2AuthorizationService authorizationService(
            AuthorizationRepository authorizationRepository,
            RegisteredClientRepository registeredClientRepository) {
        return new JpaOAuth2AuthorizationService(authorizationRepository, registeredClientRepository);

    }

    @Bean
    public OAuth2AuthorizationConsentService authorizationConsentService(
            AuthorizationConsentRepository consentRepository,
            RegisteredClientRepository registeredClientRepository) {
        return new JpaOAuth2AuthorizationConsentService(consentRepository, registeredClientRepository);
    }

    @Bean
    OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer() {
        return context -> {
            if (context.getTokenType() == OAuth2TokenType.ACCESS_TOKEN) {
                Authentication principal = context.getPrincipal();
                Set<String> authorities = principal.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .filter(authority -> authority.startsWith("ROLE_"))
                        .map(authority -> authority.substring(5))
                        .collect(Collectors.toSet());
                context.getClaims().claim("roles", authorities);
            }
        };
    }
}
