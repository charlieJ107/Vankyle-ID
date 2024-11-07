package com.vankyle.id.data.repositories;

import com.vankyle.id.data.entities.ScopeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ScopeRepository extends JpaRepository<ScopeEntity, Long> {
    Optional<ScopeEntity> findByScopeName(String scope);
    boolean existsByScopeName(String scope);
}
