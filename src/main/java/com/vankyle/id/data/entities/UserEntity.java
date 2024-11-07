package com.vankyle.id.data.entities;

import com.vankyle.id.data.converters.AuthorityConverter;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

/**
 * JPA entity mapping the domain class(interface) {@link org.springframework.security.core.userdetails.UserDetails},
 * which implemented by {@link org.springframework.security.core.userdetails.User
 */
@Data
@Entity(name = "users")
public class UserEntity implements Serializable{

    @Serial
    private static final long serialVersionUID = 4887604170063858081L;
    @Id
    @GeneratedValue
    private Long id;

    private String password;

    private String username;

    @ElementCollection(fetch = FetchType.EAGER)
    @Convert(converter = AuthorityConverter.class)
    private Set<GrantedAuthority> authorities;

    private boolean accountNonExpired;

    private boolean accountNonLocked;

    private boolean credentialsNonExpired;

    private boolean enabled;

    private String phone;
    private String email;
    private boolean phoneVerified;
    private boolean emailVerified;
    private String picture;

}
