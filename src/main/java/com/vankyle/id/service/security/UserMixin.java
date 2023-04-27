package com.vankyle.id.service.security;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

@Data
public class UserMixin implements Serializable {
    @Serial
    private static final long serialVersionUID = -2446786708085434543L;
    private String password;
    private String username;
    private Set<GrantedAuthority> authorities;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private boolean enabled;
    private byte[] verificationSecret;
    private boolean f2aEnabled;
    private String name;
    private String email;
    private boolean emailVerified;
    private String phone;
    private boolean phoneVerified;
    private String picture;
}
