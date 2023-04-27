package com.vankyle.id.data.repository;

import com.vankyle.id.data.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, String> {
    Client findByClientId(String clientId);
}
