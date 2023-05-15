package com.vankyle.id.config.jwt;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

public class RoleBasedJwtAuthenticationConverter extends JwtAuthenticationConverter {
    public RoleBasedJwtAuthenticationConverter() {
        super();
        this.setJwtGrantedAuthoritiesConverter(new RoleBasedJwtGrantedAuthoritiesConverter());
    }
}
