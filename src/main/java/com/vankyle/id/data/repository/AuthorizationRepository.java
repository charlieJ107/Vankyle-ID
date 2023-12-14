package com.vankyle.id.data.repository;

import com.vankyle.id.data.entity.Authorization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface AuthorizationRepository extends JpaRepository<Authorization, String> {

    Optional<Authorization> findByState(String state);

    Optional<Authorization> findByAuthorizationCode(String authorizationCode);

    Optional<Authorization> findByAccessToken(String accessToken);

    Optional<Authorization> findByRefreshToken(String refreshToken);

    Optional<Authorization> findByIdToken(String idToken);

    Optional<Authorization> findByUserCode(String userCode);

    Optional<Authorization> findByDeviceCode(String deviceCode);

    Optional<Authorization> findAuthorizationByAuthorizationCodeOrAccessTokenOrRefreshToken(
            String authorizationCode,
            String accessToken,
            String refreshToken
    );

}
