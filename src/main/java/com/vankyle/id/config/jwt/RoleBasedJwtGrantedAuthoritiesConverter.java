package com.vankyle.id.config.jwt;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.log.LogMessage;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * A {@link Converter} that takes a {@link Jwt} and extracts the Authorities from it.
 * Rewritten from {@link org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter}
 */
public final class RoleBasedJwtGrantedAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    private final Log logger = LogFactory.getLog(getClass());

    private static final String DEFAULT_AUTHORITY_PREFIX = "SCOPE_";
    private static final Collection<String> WELL_KNOWN_AUTHORITIES_CLAIM_NAMES = Arrays.asList("scope", "scp");
    private String authorityPrefix = DEFAULT_AUTHORITY_PREFIX;
    private String authoritiesClaimName;

    private static final String DEFAULT_ROLE_PREFIX = "ROLE_";
    private static final Collection<String> WELL_KNOWN_ROLES_CLAIM_NAMES = Arrays.asList("role", "rol", "roles");
    private String rolePrefix = DEFAULT_ROLE_PREFIX;
    private String rolesClaimName;

    /**
     * Extract {@link GrantedAuthority}s from the given {@link Jwt}.
     * @param jwt The {@link Jwt} token
     * @return The {@link GrantedAuthority authorities} read from the token scopes
     */
    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        for (String authority : getAuthorities(jwt)) {
            grantedAuthorities.add(new SimpleGrantedAuthority(this.authorityPrefix + authority));
        }
        // Add roles to granted authorities
        for (String role : getRoles(jwt)) {
            if (role.startsWith(this.rolePrefix)) {
                grantedAuthorities.add(new SimpleGrantedAuthority(role));
                continue;
            }
            grantedAuthorities.add(new SimpleGrantedAuthority(this.rolePrefix + role));
        }
        return grantedAuthorities;
    }

    /**
     * Sets the prefix to use for {@link GrantedAuthority authorities} mapped by this
     * converter. Defaults to
     * {@link RoleBasedJwtGrantedAuthoritiesConverter#DEFAULT_AUTHORITY_PREFIX}.
     * @param authorityPrefix The authority prefix
     * @since 5.2
     */
    public void setAuthorityPrefix(String authorityPrefix) {
        Assert.notNull(authorityPrefix, "authorityPrefix cannot be null");
        this.authorityPrefix = authorityPrefix;
    }

    /**
     * Sets the prefix to use for {@link GrantedAuthority authorities} mapped by this
     * converter. Defaults to
     * {@link RoleBasedJwtGrantedAuthoritiesConverter#DEFAULT_ROLE_PREFIX}.
     * @param rolePrefix The role prefix
     */
    public void setRolePrefix(String rolePrefix) {
        Assert.notNull(rolePrefix, "rolePrefix cannot be null");
        this.rolePrefix = rolePrefix;
    }

    /**
     * Sets the name of token claim to use for mapping {@link GrantedAuthority
     * authorities} by this converter. Defaults to
     * {@link RoleBasedJwtGrantedAuthoritiesConverter#WELL_KNOWN_AUTHORITIES_CLAIM_NAMES}.
     * @param authoritiesClaimName The token claim name to map authorities
     * @since 5.2
     */
    public void setAuthoritiesClaimName(String authoritiesClaimName) {
        Assert.hasText(authoritiesClaimName, "authoritiesClaimName cannot be empty");
        this.authoritiesClaimName = authoritiesClaimName;
    }

    /**
     * Sets the name of token claim to use for mapping {@link GrantedAuthority
     * authorities} by this converter. Defaults to
     * {@link RoleBasedJwtGrantedAuthoritiesConverter#WELL_KNOWN_ROLES_CLAIM_NAMES}.
     * @param rolesClaimName The token claim name to map roles
     */
    public void setRolesClaimName(String rolesClaimName) {
        Assert.hasText(rolesClaimName, "rolesClaimName cannot be empty");
        this.rolesClaimName = rolesClaimName;
    }

    private String getAuthoritiesClaimName(Jwt jwt) {
        if (this.authoritiesClaimName != null) {
            return this.authoritiesClaimName;
        }
        for (String claimName : WELL_KNOWN_AUTHORITIES_CLAIM_NAMES) {
            if (jwt.hasClaim(claimName)) {
                return claimName;
            }
        }
        return null;
    }

    private Collection<String> getAuthorities(Jwt jwt) {
        String claimName = getAuthoritiesClaimName(jwt);
        if (claimName == null) {
            this.logger.trace("Returning no authorities since could not find any claims that might contain scopes");
            return Collections.emptyList();
        }
        if (this.logger.isTraceEnabled()) {
            this.logger.trace(LogMessage.format("Looking for scopes in claim %s", claimName));
        }
        Object authorities = jwt.getClaim(claimName);
        if (authorities instanceof String) {
            if (StringUtils.hasText((String) authorities)) {
                return Arrays.asList(((String) authorities).split(" "));
            }
            return Collections.emptyList();
        }
        if (authorities instanceof Collection) {
            return castAuthoritiesToCollection(authorities);
        }
        return Collections.emptyList();
    }

    private Collection<String> getRoles(Jwt jwt) {
        String claimName = getRoleClaimName(jwt);
        if (claimName == null) {
            this.logger.trace("Returning no roles since could not find any claims that might contain scopes");
            return Collections.emptyList();
        }
        if (this.logger.isTraceEnabled()) {
            this.logger.trace(LogMessage.format("Looking for roles in claim %s", claimName));
        }
        Object roles = jwt.getClaim(claimName);
        if (roles instanceof String) {
            if (StringUtils.hasText((String) roles)) {
                return Arrays.asList(((String) roles).split(" "));
            }
            return Collections.emptyList();
        }
        if (roles instanceof Collection) {
            return castAuthoritiesToCollection(roles);
        }
        return Collections.emptyList();
    }

    private String getRoleClaimName(Jwt jwt) {
        if (this.rolesClaimName != null) {
            return this.rolesClaimName;
        }
        for (String claimName : WELL_KNOWN_ROLES_CLAIM_NAMES) {
            if (jwt.hasClaim(claimName)) {
                return claimName;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private Collection<String> castAuthoritiesToCollection(Object authorities) {
        return (Collection<String>) authorities;
    }
}
