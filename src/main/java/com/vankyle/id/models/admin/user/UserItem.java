package com.vankyle.id.models.admin.user;

import com.vankyle.id.service.security.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class UserItem implements Serializable {
    public UserItem(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.accountExpired = !user.isAccountNonExpired();
        this.accountLocked = !user.isAccountNonLocked();
        this.credentialsExpired = !user.isCredentialsNonExpired();
        this.roles = user.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .filter(authority -> authority.startsWith("ROLE_"))
                .map(authority -> authority.substring(5))
                .collect(Collectors.toSet());
        this.authorities = user.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .filter(authority -> !authority.startsWith("ROLE_")).collect(Collectors.toSet());
        this.enabled = user.isEnabled();
        this.mfaEnabled = user.isMfaEnabled();
        this.name = user.getName();
        this.email = user.getEmail();
        this.emailVerified = user.isEmailVerified();
        this.phone = user.getPhone();
        this.phoneVerified = user.isPhoneVerified();
        this.picture = user.getPicture();
    }

    @Serial
    private static final long serialVersionUID = 1970157782535593402L;
    private String id;
    private String username;
    private String password;
    private boolean accountExpired;
    private boolean accountLocked;
    private boolean credentialsExpired;
    private Set<String> roles;
    private Set<String> authorities;
    private boolean enabled;
    private boolean mfaEnabled;
    private String name;
    private String email;
    private boolean emailVerified;
    private String phone;
    private boolean phoneVerified;
    private String picture;
}
