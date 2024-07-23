package com.vankyle.id.data.entities;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

/**
 * The entity class for the {@link org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent}
 */
@Data
@Entity
public class AuthorizationConsentEntity {
    @Id
    private Long id;
    private String registeredClientId;
    private String principalName;
    @ElementCollection
    private Set<GrantedAuthority> authorities;
}
