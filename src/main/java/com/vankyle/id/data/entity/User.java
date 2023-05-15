package com.vankyle.id.data.entity;

import com.vankyle.id.data.utils.GrantedAuthorityConverter;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

/**
 * JPA entity mapping the domain class(interface) {@link org.springframework.security.core.userdetails.UserDetails},
 * which implemented by {@link org.springframework.security.core.userdetails.User
 */

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "vankyle_user", indexes = {
        @Index(name = "idx_user_username", columnList = "username")
})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(unique = true)
    private String username;
    private String password;
    private boolean isAccountNonExpired;
    private boolean isAccountNonLocked;
    private boolean isCredentialsNonExpired;
    private boolean isEnabled;
    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    @Convert(converter = GrantedAuthorityConverter.class)
    private Set<GrantedAuthority> authorities;
    private byte[] securityStamp;
    private boolean isMfaEnabled;
    private String name;
    private String email;
    private boolean emailVerified;
    private String phone;
    private boolean phoneVerified;
    private String picture;

}
