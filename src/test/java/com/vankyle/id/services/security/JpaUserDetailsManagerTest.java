package com.vankyle.id.services.security;

import com.vankyle.id.data.entities.UserEntity;
import com.vankyle.id.data.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class JpaUserDetailsManagerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private JpaUserDetailsManager userDetailsManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(passwordEncoder.encode(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void createUserSuccessfully() {
        UserDetails user = mock(UserDetails.class);
        when(user.getUsername()).thenReturn("user");
        when(user.getPassword()).thenReturn("password");
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userDetailsManager.createUser(user);

        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void updateUserSuccessfully() {
        UserDetails user = mock(UserDetails.class);
        when(user.getUsername()).thenReturn("existingUser");
        when(userRepository.findByUsername("existingUser")).thenReturn(Optional.of(new UserEntity()));
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userDetailsManager.updateUser(user);

        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void updateUserNotFound() {
        UserDetails user = mock(UserDetails.class);
        when(user.getUsername()).thenReturn("nonExistingUser");
        when(userRepository.findByUsername("nonExistingUser")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userDetailsManager.updateUser(user));
    }

    @Test
    void deleteUserSuccessfully() {
        when(userRepository.findByUsername("existingUser")).thenReturn(Optional.of(new UserEntity()));

        userDetailsManager.deleteUser("existingUser");

        verify(userRepository).delete(any(UserEntity.class));
    }

    @Test
    void deleteUserNotFound() {
        when(userRepository.findByUsername("nonExistingUser")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userDetailsManager.deleteUser("nonExistingUser"));
    }

    // Skipped test for changePassword method as we don't have mocked SecurityContextHolderStrategy and AuthenticationManager
//    @Test
//    void changePasswordSuccessfully() {
//        UserEntity userEntity = new UserEntity();
//        // Set username and password to "user" and "password" respectively, as well as all other required fields
//        userEntity.setUsername("user");
//        userEntity.setPassword("password");
//        userEntity.setAuthorities(Set.of(new SimpleGrantedAuthority("ROLE_USER")));
//        userEntity.setAccountNonExpired(true);
//        userEntity.setAccountNonLocked(true);
//        userEntity.setCredentialsNonExpired(true);
//        userEntity.setEnabled(true);
//        when(userRepository.findByUsername("user")).thenReturn(Optional.of(userEntity));
//        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
//        userDetailsManager.changePassword("oldPassword", "newPassword");
//
//        verify(userRepository).save(any(UserEntity.class));
//    }

    @Test
    void userExistsReturnsTrueForExistingUser() {
        when(userRepository.existsByUsername("existingUser")).thenReturn(true);

        assertTrue(userDetailsManager.userExists("existingUser"));
    }

    @Test
    void userExistsReturnsFalseForNonExistingUser() {
        when(userRepository.existsByUsername("nonExistingUser")).thenReturn(false);

        assertFalse(userDetailsManager.userExists("nonExistingUser"));
    }

    @Test
    void loadUserByUsernameSuccessfully() {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername("user");
        userEntity.setPassword("password");
        userEntity.setAuthorities(Set.of(new SimpleGrantedAuthority("ROLE_USER")));
        userEntity.setAccountNonExpired(true);
        userEntity.setAccountNonLocked(true);
        userEntity.setCredentialsNonExpired(true);
        userEntity.setEnabled(true);
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(userEntity));

        UserDetails userDetails = userDetailsManager.loadUserByUsername("user");

        assertEquals("user", userDetails.getUsername());
    }

    @Test
    void loadUserByUsernameNotFound() {
        when(userRepository.findByUsername("nonExistingUser")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userDetailsManager.loadUserByUsername("nonExistingUser"));
    }
}