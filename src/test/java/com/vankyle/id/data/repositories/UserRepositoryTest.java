package com.vankyle.id.data.repositories;

import com.vankyle.id.data.entities.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@DataJpaTest
public class UserRepositoryTest {

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findByUsername_whenUsernameExists_returnsUserEntity() {
        String username = "existingUser";
        UserEntity userEntity = new UserEntity();
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(userEntity));

        Optional<UserEntity> result = userRepository.findByUsername(username);

        assertTrue(result.isPresent());
    }

    @Test
    void findByUsername_whenUsernameDoesNotExist_returnsEmpty() {
        String username = "nonExistentUser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        Optional<UserEntity> result = userRepository.findByUsername(username);

        assertFalse(result.isPresent());
    }

    @Test
    void findByEmail_whenEmailExists_returnsUserEntity() {
        String email = "user@example.com";
        UserEntity userEntity = new UserEntity();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));

        Optional<UserEntity> result = userRepository.findByEmail(email);

        assertTrue(result.isPresent());
    }

    @Test
    void findByEmail_whenEmailDoesNotExist_returnsEmpty() {
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        Optional<UserEntity> result = userRepository.findByEmail(email);

        assertFalse(result.isPresent());
    }

    @Test
    void findByPhone_whenPhoneExists_returnsUserEntity() {
        String phone = "1234567890";
        UserEntity userEntity = new UserEntity();
        when(userRepository.findByPhone(phone)).thenReturn(Optional.of(userEntity));

        Optional<UserEntity> result = userRepository.findByPhone(phone);

        assertTrue(result.isPresent());
    }

    @Test
    void findByPhone_whenPhoneDoesNotExist_returnsEmpty() {
        String phone = "0987654321";
        when(userRepository.findByPhone(phone)).thenReturn(Optional.empty());

        Optional<UserEntity> result = userRepository.findByPhone(phone);

        assertFalse(result.isPresent());
    }
}