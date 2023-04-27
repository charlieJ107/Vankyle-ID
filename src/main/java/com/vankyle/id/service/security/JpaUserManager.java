package com.vankyle.id.service.security;

import com.vankyle.id.data.repository.UserRepository;
import lombok.Setter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.log.LogMessage;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.cache.NullUserCache;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Assert;

import java.security.SecureRandom;
import java.util.Set;

public class JpaUserManager implements UserManager {
    private final UserRepository userRepository;
    private static final Log logger = LogFactory.getLog(JpaUserManager.class);
    private SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder
            .getContextHolderStrategy();
    private final PasswordEncoder passwordEncoder;

    @Setter
    private AuthenticationManager authenticationManager;
    private UserCache userCache = new NullUserCache();

    public JpaUserManager(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void createUser(UserDetails user) {
        var entity = userRepository.findByUsername(user.getUsername());
        if (entity != null) {
            throw new UsernameNotFoundException("User already exists");
        }
        entity = userDetailsToUserEntity(user);
        entity.setPassword(passwordEncoder.encode(user.getPassword()));
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[64];
        random.nextBytes(bytes);
        entity.setVerificationSecret(bytes);
        userRepository.save(entity);
    }

    @Override
    public void updateUser(UserDetails user) {
        var entity = userRepository.findByUsername(user.getUsername());
        if (entity == null) {
            throw new UsernameNotFoundException("User not found");
        }
        userDetailsToUserEntity(user);
        entity.setPassword(user.getPassword());
        entity.setAuthorities(Set.copyOf(user.getAuthorities()));
        entity.setAccountNonExpired(user.isAccountNonExpired());
        entity.setAccountNonLocked(user.isAccountNonLocked());
        entity.setCredentialsNonExpired(user.isCredentialsNonExpired());
        entity.setEnabled(user.isEnabled());
        userRepository.save(entity);
    }

    @Override
    public void deleteUser(String username) {
        userRepository.deleteByUsername(username);
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        Authentication currentUser = this.securityContextHolderStrategy.getContext().getAuthentication();
        if (currentUser == null) {
            // This would indicate bad coding somewhere
            throw new AccessDeniedException(
                    "Can't change password as no Authentication object found in context " + "for current user.");
        }
        var user = userRepository.findByUsername(currentUser.getName());
        if (user == null){
            throw new AccessDeniedException("No such user");
        }
        String username = currentUser.getName();
        // If an authentication manager has been set, re-authenticate the user with the
        // supplied password.
        if (this.authenticationManager != null) {
            logger.debug(LogMessage.format("Re-authenticating user '%s' for password change request.", username));
            this.authenticationManager
                    .authenticate(UsernamePasswordAuthenticationToken.unauthenticated(username, oldPassword));
        } else {
            if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
                logger.debug("Re-authentication is not configured (this is OK if you are using stateless authentication), "
                        + "but the existing password is not correct. Password not changed.");
                throw new AccessDeniedException("Re-authentication is not configured (this is OK if you are using stateless authentication), "
                        + "but the existing password is not correct. Password not changed.");
            }
            logger.debug("No authentication manager set. Password won't be re-checked.");
        }
        logger.debug("Changing password for user '" + username + "'");
        var entity = userRepository.findByUsername(username);
        if (entity == null) {
            throw new UsernameNotFoundException("User not found");
        }
        entity.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(entity);
        Authentication authentication = createNewAuthentication(currentUser, newPassword);
        SecurityContext context = this.securityContextHolderStrategy.createEmptyContext();
        context.setAuthentication(authentication);
        this.securityContextHolderStrategy.setContext(context);
        this.userCache.removeUserFromCache(username);
    }

    protected Authentication createNewAuthentication(Authentication currentAuth, String newPassword) {
        UserDetails user = loadUserByUsername(currentAuth.getName());
        UsernamePasswordAuthenticationToken newAuthentication = UsernamePasswordAuthenticationToken.authenticated(user,
                null, user.getAuthorities());
        newAuthentication.setDetails(currentAuth.getDetails());
        return newAuthentication;
    }

    @Override
    public boolean userExists(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Assert.notNull(username, "Username may not be null");
        var entity = userRepository.findByUsername(username);
        if (entity == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return entityToUserDetails(entity);
    }

    private UserDetails entityToUserDetails(com.vankyle.id.data.entity.User user) {
        return new User(
                user.getUsername(),
                user.getPassword(),
                user.isAccountNonExpired(),
                user.isAccountNonLocked(),
                user.isCredentialsNonExpired(),
                user.isEnabled(),
                user.getAuthorities()
        );
    }

    private com.vankyle.id.data.entity.User userDetailsToUserEntity(UserDetails userDetails) {
        var user = new com.vankyle.id.data.entity.User();
        userDetailsToEntity(userDetails, user);
        return user;
    }

    private void userDetailsToEntity(UserDetails userDetails, com.vankyle.id.data.entity.User entity) {
        entity.setUsername(userDetails.getUsername());
        entity.setPassword(userDetails.getPassword());
        entity.setAuthorities(Set.copyOf(userDetails.getAuthorities()));
        entity.setAccountNonExpired(userDetails.isAccountNonExpired());
        entity.setAccountNonLocked(userDetails.isAccountNonLocked());
        entity.setCredentialsNonExpired(userDetails.isCredentialsNonExpired());
        entity.setEnabled(userDetails.isEnabled());
    }

    private com.vankyle.id.data.entity.User userToUserEntity(User user) {
        var entity = userDetailsToUserEntity(user);
        userToUserEntity(user, entity);
        return entity;
    }

    private void userToUserEntity(User user, com.vankyle.id.data.entity.User entity) {
        userDetailsToEntity(user, entity);
        entity.setVerificationSecret(user.getSecurityStamp());
        entity.setF2aEnabled(user.isF2aEnabled());
        entity.setName(user.getName());
        entity.setEmail(user.getEmail());
        entity.setEmailVerified(user.isEmailVerified());
        entity.setPhone(user.getPhone());
        entity.setPhoneVerified(user.isPhoneVerified());
        entity.setPicture(user.getPicture());
    }

    private User userEntityToUser(com.vankyle.id.data.entity.User entity) {
        return new User(
                entity.getUsername(),
                entity.getPassword(),
                entity.isAccountNonExpired(),
                entity.isAccountNonLocked(),
                entity.isCredentialsNonExpired(),
                entity.isEnabled(),
                entity.getAuthorities(),
                entity.getVerificationSecret(),
                entity.isF2aEnabled(),
                entity.getName(),
                entity.getEmail(),
                entity.isEmailVerified(),
                entity.getPhone(),
                entity.isPhoneVerified(),
                entity.getPicture()
        );
    }

    /**
     * * Optionally sets the UserCache if one is in use in the application. This allows the
     * user to be removed from the cache after updates have taken place to avoid stale
     * data.
     *
     * @param userCache the cache used by the AuthenticationManager.
     */
    public void setUserCache(UserCache userCache) {
        Assert.notNull(userCache, "userCache cannot be null");
        this.userCache = userCache;
    }

    /**
     * Sets the {@link SecurityContextHolderStrategy} to use. The default action is to use
     * the {@link SecurityContextHolderStrategy} stored in {@link SecurityContextHolder}.
     *
     * @since 5.8
     */
    public void setSecurityContextHolderStrategy(SecurityContextHolderStrategy securityContextHolderStrategy) {
        Assert.notNull(securityContextHolderStrategy, "securityContextHolderStrategy cannot be null");
        this.securityContextHolderStrategy = securityContextHolderStrategy;
    }


    @Override
    public void createUser(User user) throws UsernameAlreadyExistsException {
        var entity = userRepository.findByUsername(user.getUsername());
        if (entity != null) {
            throw new UsernameAlreadyExistsException("User already exists");
        }
        entity = userToUserEntity(user);
        entity.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(entity);
    }

    @Override
    public void updateUser(User user) {
        var entity = userRepository.findByUsername(user.getUsername());
        if (entity == null) {
            throw new UsernameNotFoundException("User not found");
        }
        entity.setUsername(user.getUsername());
        entity.setAccountNonExpired(user.isAccountNonExpired());
        entity.setAccountNonLocked(user.isAccountNonLocked());
        entity.setCredentialsNonExpired(user.isCredentialsNonExpired());
        entity.setEnabled(user.isEnabled());
        entity.setF2aEnabled(user.isF2aEnabled());
        entity.setEmail(user.getEmail());
        entity.setEmailVerified(user.isEmailVerified());
        entity.setPhone(user.getPhone());
        entity.setPhoneVerified(user.isPhoneVerified());
        entity.setPicture(user.getPicture());
        entity.setName(user.getName());
        userRepository.saveAndFlush(entity);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public User findByUsername(String username) {
        var userEntity = userRepository.findByUsername(username);
        return userEntityToUser(userEntity);
    }

    @Override
    public User findByEmail(String email) {
        var entity = userRepository.findByEmail(email);
        if (entity == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return userEntityToUser(entity);
    }

    @Override
    public void changePassword(User user, String oldPassword, String newPassword) {
        var entity = userRepository.findByUsername(user.getUsername());
        if (entity == null) {
            throw new UsernameNotFoundException("User not found");
        }
        if (!passwordEncoder.matches(oldPassword, entity.getPassword())) {
            throw new BadCredentialsException("Bad credentials");
        }
        entity.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(entity);
    }

    @Override
    public void resetPassword(User user, String password) {
        var entity = userRepository.findByUsername(user.getUsername());
        if (entity == null) {
            throw new UsernameNotFoundException("User not found");
        }
        entity.setPassword(passwordEncoder.encode(password));
        userRepository.save(entity);
    }

    @Override
    public void resetPassword(String username, String password) {
        var entity = userRepository.findByUsername(username);
        if (entity == null) {
            throw new UsernameNotFoundException("User not found");
        }
        entity.setPassword(passwordEncoder.encode(password));
        userRepository.save(entity);
    }

    @Override
    public void confirmEmail(String username) {
        var entity = userRepository.findByUsername(username);
        if (entity == null) {
            throw new UsernameNotFoundException("User not found");
        }
        entity.setAccountNonLocked(true);
        entity.setEmailVerified(true);
        userRepository.save(entity);
    }

    @Override
    public void lockUser(String username) {
        var entity = userRepository.findByUsername(username);
        if (entity == null) {
            throw new UsernameNotFoundException("User not found");
        }
        entity.setAccountNonLocked(false);
        userRepository.save(entity);
    }

    @Override
    public void unlockUser(String username) {
        var entity = userRepository.findByUsername(username);
        if (entity == null) {
            throw new UsernameNotFoundException("User not found");
        }
        entity.setAccountNonLocked(true);
        userRepository.save(entity);
    }

    @Override
    public void updateSecurityStamp(String username) {
        var entity = userRepository.findByUsername(username);
        if (entity == null) {
            throw new UsernameNotFoundException("User not found");
        }
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[64];
        random.nextBytes(bytes);
        entity.setVerificationSecret(bytes);
        userRepository.save(entity);
    }
}
