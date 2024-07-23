package com.vankyle.id.services.oauth2;

import com.vankyle.id.data.entities.AuthorizationConsentEntity;
import com.vankyle.id.data.repositories.AuthorizationConsentRepository;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.util.Assert;

public class JpaAuthorizationConsentService implements OAuth2AuthorizationConsentService {
    private final AuthorizationConsentRepository authorizationConsentRepository;
    private final RegisteredClientRepository registeredClientRepository;

    public JpaAuthorizationConsentService(AuthorizationConsentRepository authorizationConsentRepository, RegisteredClientRepository registeredClientRepository) {
        this.authorizationConsentRepository = authorizationConsentRepository;
        this.registeredClientRepository = registeredClientRepository;
    }

    @Override
    public void save(OAuth2AuthorizationConsent authorizationConsent) {
        Assert.notNull(authorizationConsent, "authorizationConsent cannot be null");
        AuthorizationConsentEntity entity = authorizationConsentRepository
                .findByRegisteredClientIdAndPrincipalName(
                        authorizationConsent.getRegisteredClientId(),
                        authorizationConsent.getPrincipalName()
                ).orElse(new AuthorizationConsentEntity());
        entity = toEntity(authorizationConsent, entity);
        authorizationConsentRepository.save(entity);
    }

    @Override
    public void remove(OAuth2AuthorizationConsent authorizationConsent) {
        Assert.notNull(authorizationConsent, "authorizationConsent cannot be null");
        authorizationConsentRepository
                .findByRegisteredClientIdAndPrincipalName(
                        authorizationConsent.getRegisteredClientId(),
                        authorizationConsent.getPrincipalName()
                ).ifPresent(authorizationConsentRepository::delete);

    }

    @Override
    public OAuth2AuthorizationConsent findById(String registeredClientId, String principalName) {
        Assert.hasText(registeredClientId, "registeredClientId cannot be empty");
        Assert.hasText(principalName, "principalName cannot be empty");
        return authorizationConsentRepository
                .findByRegisteredClientIdAndPrincipalName(registeredClientId, principalName)
                .map(this::toObject)
                .orElse(null);
    }

    private OAuth2AuthorizationConsent toObject(AuthorizationConsentEntity entity) {
        RegisteredClient client = this.registeredClientRepository.findByClientId(entity.getRegisteredClientId());
        if (client == null) {
            throw new DataRetrievalFailureException(
                    "The RegisteredClient with id '" + entity.getRegisteredClientId() + "' was not found in the RegisteredClientRepository.");
        }
        return OAuth2AuthorizationConsent
                .withId(entity.getRegisteredClientId(), entity.getPrincipalName())
                .authorities(grantedAuthorities -> grantedAuthorities.addAll(entity.getAuthorities()))
                .build();
    }

    private AuthorizationConsentEntity toEntity(OAuth2AuthorizationConsent authorizationConsent, AuthorizationConsentEntity entity) {
        entity.setRegisteredClientId(authorizationConsent.getRegisteredClientId());
        entity.setPrincipalName(authorizationConsent.getPrincipalName());
        entity.setAuthorities(authorizationConsent.getAuthorities());
        return entity;
    }
}
