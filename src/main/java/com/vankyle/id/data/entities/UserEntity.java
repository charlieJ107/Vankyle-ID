package com.vankyle.id.data.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

/**
 * JPA entity mapping the domain class(interface) {@link org.springframework.security.core.userdetails.UserDetails},
 * which implemented by {@link org.springframework.security.core.userdetails.User
 */
@Data
@Entity
public class UserEntity implements Serializable{

    @Serial
    private static final long serialVersionUID = 4887604170063858081L;
    @Id
    @GeneratedValue
    private Long id;
    private static final Log logger = LogFactory.getLog(User.class);

    private String password;

    private String username;

    @ElementCollection(fetch = FetchType.EAGER)
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
