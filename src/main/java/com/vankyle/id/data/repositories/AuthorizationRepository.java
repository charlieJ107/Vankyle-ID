package com.vankyle.id.data.repositories;

import com.vankyle.id.data.entities.AuthorizationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorizationRepository extends JpaRepository<AuthorizationEntity, Long> {
    Optional<AuthorizationEntity> findByAuthorizationId(String authorizationId);
    Optional<AuthorizationEntity> findByState(String state);

    Optional<AuthorizationEntity> findByAuthorizationCodeValue(String authorizationCodeValue);

    Optional<AuthorizationEntity> findByRefreshTokenValue(String refreshTokenValue);

    Optional<AuthorizationEntity> findByAccessTokenValue(String accessTokenValue);

    Optional<AuthorizationEntity> findByOidcIdTokenValue(String oidcIdTokenValue);

    Optional<AuthorizationEntity> findByUserCodeValue(String userCodeValue);

    Optional<AuthorizationEntity> findByDeviceCodeValue(String deviceCodeValue);

    @Query("SELECT a FROM AuthorizationEntity a WHERE a.state = :token" +
            " OR a.authorizationCodeValue = :token" +
            " OR a.refreshTokenValue = :token " +
            " OR a.accessTokenValue = :token " +
            " OR a.oidcIdTokenValue = :token" +
            " OR a.userCodeValue = :token" +
            " OR a.deviceCodeValue = :token")
    Optional<AuthorizationEntity> findByToken(@Param("token") String token);
}
