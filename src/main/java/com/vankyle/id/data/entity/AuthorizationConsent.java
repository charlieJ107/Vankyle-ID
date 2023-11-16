package com.vankyle.id.data.entity;

import com.vankyle.id.data.utils.GrantedAuthorityConverter;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

/**
 * JPA entity mapping the domain class(interface) {@link org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent},
 */
@Data // TODO: Data annotation is not recommended for JPA entity class, it can cause performance issue
@Entity
@IdClass(AuthorizationConsent.AuthorizationConsentId.class)
public class AuthorizationConsent {
    @Id
    private String registeredClientId;
    @Id
    private String principalName;
    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    @Convert(converter = GrantedAuthorityConverter.class)
    private Set<GrantedAuthority> authorities;
    @Data
    public static class AuthorizationConsentId implements Serializable {
        @Serial
        private static final long serialVersionUID = 6836378429802151007L;
        private String registeredClientId;
        private String principalName;
    }
}
