package com.vankyle.id.service.oauth;

import com.vankyle.id.data.repository.ClientRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;

import java.util.List;

public class JpaRegisteredClientService extends JpaRegisteredClientRepository implements RegisteredClientService {
    public JpaRegisteredClientService(ClientRepository clientRepository, PasswordEncoder passwordEncoder) {
        super(clientRepository, passwordEncoder);
    }

    @Override
    public List<RegisteredClient> findAll() {
        var entities = this.clientRepository.findAll();
        return entities.stream().map(this::toRegisteredClient).toList();
    }

    @Override
    public boolean existsById(String id) {
        return this.clientRepository.existsById(id);
    }
}
