package com.vankyle.id.data.repositories;

import static org.junit.jupiter.api.Assertions.*;

import com.vankyle.id.data.entities.ClientEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@DataJpaTest
public class ClientRepositoryTest {

    @Mock
    private ClientRepository clientRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findByClientId_whenClientIdExists_returnsClientEntity() {
        String clientId = "client123";
        ClientEntity clientEntity = new ClientEntity();
        when(clientRepository.findByClientId(clientId)).thenReturn(Optional.of(clientEntity));

        Optional<ClientEntity> result = clientRepository.findByClientId(clientId);

        assertTrue(result.isPresent());
    }

    @Test
    void findByClientId_whenClientIdDoesNotExist_returnsEmpty() {
        String clientId = "nonExistentClient";
        when(clientRepository.findByClientId(clientId)).thenReturn(Optional.empty());

        Optional<ClientEntity> result = clientRepository.findByClientId(clientId);

        assertFalse(result.isPresent());
    }
}