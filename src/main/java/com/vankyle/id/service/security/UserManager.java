package com.vankyle.id.service.security;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;

import java.util.Set;

public interface UserManager extends UserDetailsManager {
    void createUser(User user) throws UsernameAlreadyExistsException;

    /**
     * Update user information, except authorities
     * @param user user information
     * @throws UsernameNotFoundException if user not found
     */
    void updateUser(User user) throws UsernameNotFoundException;

    boolean existsByEmail(String email);

    User findByUsername(String username);

    User findByEmail(String email);
    User findById(String id);
    Set<User> findAllUsers();
    void changePassword(User user,String oldPassword, String newPassword);
    void resetPassword(User user, String password);
    void resetPassword(String username, String password);
    void confirmEmail(String username);

    void lockUser(String username);
    void unlockUser(String username);

    void updateSecurityStamp(String username);
}
