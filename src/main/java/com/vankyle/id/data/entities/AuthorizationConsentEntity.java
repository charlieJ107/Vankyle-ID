package com.vankyle.id.data.entities;

import com.vankyle.id.data.converters.AuthorityConverter;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

/**
 * The entity class for the {@link org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent}
 */
@Data
@Entity(name = "consents")
public class AuthorizationConsentEntity {
    @Id
    private Long id;
    private String registeredClientId;
    private String principalName;
    @ElementCollection(fetch = FetchType.EAGER)
    @Convert(converter = AuthorityConverter.class)
    private Set<GrantedAuthority> authorities;
}
