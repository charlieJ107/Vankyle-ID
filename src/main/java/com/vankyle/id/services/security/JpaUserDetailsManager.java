package com.vankyle.id.services.security;

import com.vankyle.id.data.entities.UserEntity;
import com.vankyle.id.data.repositories.UserRepository;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.log.LogMessage;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;

import java.util.stream.Collectors;


/**
 * JpaUserDetailsManager
 * Refers to {@link org.springframework.security.provisioning.JdbcUserDetailsManager}
 * and {@link org.springframework.security.provisioning.InMemoryUserDetailsManager}
 */
public class JpaUserDetailsManager implements UserDetailsManager {
    @Getter
    @Setter
    private SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder
            .getContextHolderStrategy();
    private AuthenticationManager authenticationManager;
    protected final Log logger = LogFactory.getLog(getClass());
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public JpaUserDetailsManager(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void createUser(UserDetails user) {
        userRepository.save(toUserEntity(user, new UserEntity()));
    }

    @Override
    public void updateUser(UserDetails user) {
        var userEntity = userRepository.findByUsername(user.getUsername()).orElse(null);
        if (userEntity == null) {
            throw new UsernameNotFoundException("User " + user.getUsername() + " not found");
        }
        userRepository.save(toUserEntity(user, userEntity));
    }

    @Override
    public void deleteUser(String username) {
        var userEntity = userRepository.findByUsername(username).orElse(null);
        if (userEntity != null) {
            userRepository.delete(userEntity);
        } else {
            throw new UsernameNotFoundException("User " + username + " not found");
        }
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        Authentication currentUser = this.securityContextHolderStrategy.getContext().getAuthentication();
        if (currentUser == null) {
            // This would indicate bad coding somewhere
            throw new AccessDeniedException(
                    "Can't change password as no Authentication object found in context " + "for current user.");
        }
        String username = currentUser.getName();
        // If an authentication manager has been set, re-authenticate the user with the
        // supplied password.
        if (this.authenticationManager != null) {
            this.logger.debug(LogMessage.format("Reauthenticating user '%s' for password change request.", username));
            this.authenticationManager
                    .authenticate(UsernamePasswordAuthenticationToken.unauthenticated(username, oldPassword));
        } else {
            this.logger.debug("No authentication manager set. Password won't be re-checked.");
        }
        this.logger.debug("Changing password for user '" + username + "'");
        UserEntity userEntity = userRepository.findByUsername(username).orElse(null);
        if (userEntity != null) {
            userEntity.setPassword(newPassword);
            userRepository.save(userEntity);
        }
        Authentication authentication = createNewAuthentication(currentUser, newPassword);
        SecurityContext context = this.securityContextHolderStrategy.createEmptyContext();
        context.setAuthentication(authentication);
        this.securityContextHolderStrategy.setContext(context);
    }

    @Override
    public boolean userExists(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByUsername(username).orElse(null);
        if (userEntity == null) {
            throw new UsernameNotFoundException("User " + username + " not found");
        }
        return User.withUsername(username)
                .password(userEntity.getPassword())
                .authorities(userEntity.getAuthorities())
                .accountExpired(!userEntity.isAccountNonExpired())
                .accountLocked(!userEntity.isAccountNonLocked())
                .credentialsExpired(!userEntity.isCredentialsNonExpired())
                .disabled(!userEntity.isEnabled())
                .passwordEncoder(passwordEncoder::encode)
                .build();
    }

    protected UserDetails toUserDetails(UserEntity userEntity) {
        return User.withUsername(userEntity.getUsername())
                .password(userEntity.getPassword())
                .authorities(userEntity.getAuthorities())
                .accountExpired(!userEntity.isAccountNonExpired())
                .accountLocked(!userEntity.isAccountNonLocked())
                .credentialsExpired(!userEntity.isCredentialsNonExpired())
                .disabled(!userEntity.isEnabled())
                .authorities(userEntity.getAuthorities())
                .passwordEncoder(passwordEncoder::encode)
                .build();
    }

    protected UserEntity toUserEntity(UserDetails userDetails, UserEntity userEntity) {
        userEntity.setUsername(userDetails.getUsername());
        userEntity.setPassword(userDetails.getPassword());
        userEntity.setAccountNonExpired(userDetails.isAccountNonExpired());
        userEntity.setAccountNonLocked(userDetails.isAccountNonLocked());
        userEntity.setCredentialsNonExpired(userDetails.isCredentialsNonExpired());
        userEntity.setEnabled(!userDetails.isEnabled());
        userEntity.setAuthorities(userDetails.getAuthorities().stream().map(grantedAuthority ->
                (GrantedAuthority) grantedAuthority
        ).collect(Collectors.toSet()));
        return userEntity;
    }

    protected Authentication createNewAuthentication(Authentication currentAuth, String newPassword) {
        UserDetails user = loadUserByUsername(currentAuth.getName());
        UsernamePasswordAuthenticationToken newAuthentication = UsernamePasswordAuthenticationToken.authenticated(user,
                null, user.getAuthorities());
        newAuthentication.setDetails(currentAuth.getDetails());
        return newAuthentication;
    }
}
