package com.vankyle.id.service.oauth;

import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

import java.util.List;

public interface RegisteredClientService extends RegisteredClientRepository {
    List<RegisteredClient> findAll();
    boolean existsById(String id);
}
