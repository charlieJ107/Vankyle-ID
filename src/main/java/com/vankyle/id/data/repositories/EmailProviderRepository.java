package com.vankyle.id.data.repositories;

import com.vankyle.id.data.entities.EmailProviderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailProviderRepository extends JpaRepository<EmailProviderEntity, Long> {
}
