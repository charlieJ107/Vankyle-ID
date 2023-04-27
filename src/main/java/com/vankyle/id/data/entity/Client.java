package com.vankyle.id.data.entity;

import com.vankyle.id.data.utils.AuthorizationGrantTypeConverter;
import com.vankyle.id.data.utils.ClientAuthenticationMethodConverter;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

import java.time.Instant;
import java.util.Objects;
import java.util.Set;


/**
 * JPA entity mapping the domain class(interface) {@link org.springframework.security.oauth2.server.authorization.client.RegisteredClient}
 */
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "client", indexes = {
        @Index(name = "idx_client_client_id", columnList = "clientId")
})
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(unique = true)
    private String clientId;
    private Instant clientIdIssuedAt;
    private String clientSecret;
    private Instant clientSecretExpiresAt;
    private String clientName;
    @ElementCollection(fetch = FetchType.EAGER)
    @Convert(converter = ClientAuthenticationMethodConverter.class)
    private Set<ClientAuthenticationMethod> clientAuthenticationMethods;
    @ElementCollection(fetch = FetchType.EAGER)
    @Convert(converter = AuthorizationGrantTypeConverter.class)
    private Set<AuthorizationGrantType> authorizationGrantTypes;
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> redirectUris;
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> scopes;
    @Embedded
    private ClientSettingsData clientSettings;
    @Embedded
    private TokenSettingsData tokenSettings;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Client client = (Client) o;
        return getId() != null && Objects.equals(getId(), client.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

