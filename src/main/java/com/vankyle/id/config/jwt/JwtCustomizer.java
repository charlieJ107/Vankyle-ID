package com.vankyle.id.config.jwt;

import com.vankyle.id.service.security.User;
import com.vankyle.id.service.security.UserManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Customize the JWT token.
 * @see org.springframework.security.oauth2.server.authorization.web.OAuth2TokenEndpointFilter
 * @see org.springframework.security.oauth2.server.authorization.authentication.OAuth2RefreshTokenAuthenticationProvider
 */
public class JwtCustomizer implements OAuth2TokenCustomizer<JwtEncodingContext> {
    private final UserManager userManager;
    public JwtCustomizer(UserManager userManager) {
        this.userManager = userManager;
    }
    @Override
    public void customize(JwtEncodingContext context) {
        User user = userManager.findByUsername(context.getPrincipal().getName());
        if (context.getTokenType() == OAuth2TokenType.ACCESS_TOKEN) {
            Authentication principal = context.getPrincipal();
            Set<String> authorities = principal.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .filter(authority -> authority.startsWith("ROLE_"))
                    .map(authority -> authority.substring(5))
                    .collect(Collectors.toSet());
            context.getClaims().claim("roles", authorities);
            context.getClaims().claim("uid", user.getId());
        } else if (OidcParameterNames.ID_TOKEN.equals(context.getTokenType().getValue())) {
            OidcUserInfo.Builder builder = OidcUserInfo.builder()
                    .subject(user.getUsername())
                    .name(user.getName())
                    .claim("uid", user.getId());

            OAuth2Authorization oAuth2Authorization = context.getAuthorization();
            if (oAuth2Authorization != null) {
                Set<String> requestedScopes = oAuth2Authorization.getAuthorizedScopes();
//               if (requestedScopes.contains(OidcScopes.ADDRESS)){
//                   builder.address(user.getAddress()); // TODO: User address not implemented
//               }
                if (requestedScopes.contains(OidcScopes.EMAIL)) {
                    builder.email(user.getEmail());
                    builder.emailVerified(user.isEmailVerified());
                }
                if (requestedScopes.contains(OidcScopes.PHONE)) {
                    builder.phoneNumber(user.getPhone());
                    builder.phoneNumberVerified(user.isPhoneVerified());
                }
                if (requestedScopes.contains(OidcScopes.PROFILE)){
                    builder.name(user.getName());
                    builder.picture(user.getPicture());
                    // TODO: Other user profile not implemented
                    //  Done StandardClaimNames.NAME
                    //       StandardClaimNames.FAMILY_NAME
                    //       StandardClaimNames.GIVEN_NAME
                    //       StandardClaimNames.MIDDLE_NAME
                    //       StandardClaimNames.NICKNAME
                    //  (??) StandardClaimNames.PREFERRED_USERNAME
                    //  (??) StandardClaimNames.PROFILE
                    //  Done StandardClaimNames.PICTURE
                    //       StandardClaimNames.WEBSITE
                    //       StandardClaimNames.GENDER
                    //       StandardClaimNames.BIRTHDATE
                    //       StandardClaimNames.ZONEINFO
                    //       StandardClaimNames.LOCALE
                    //       StandardClaimNames.UPDATED_AT
                }
            }
            Map<String, Object> jwtClaims = builder.build().getClaims();
            context.getClaims().claims(claims->claims.putAll(jwtClaims));
        }
    }

}
