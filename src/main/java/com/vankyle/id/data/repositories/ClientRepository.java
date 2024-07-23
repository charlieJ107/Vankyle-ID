package com.vankyle.id.data.repositories;

import com.vankyle.id.data.entities.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<ClientEntity, Long> {
    Optional<ClientEntity> findByRegisteredClientId(String registeredClientId);
    Optional<ClientEntity> findByClientId(String clientId);
}
