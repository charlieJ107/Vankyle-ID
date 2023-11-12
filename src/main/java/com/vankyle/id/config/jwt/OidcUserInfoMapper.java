package com.vankyle.id.config.jwt;

import com.vankyle.id.service.security.User;

import com.vankyle.id.service.security.UserManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcUserInfoAuthenticationContext;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Function;

/**
 * @see org.springframework.security.oauth2.server.authorization.oidc.web.OidcUserInfoEndpointFilter
 */
public class OidcUserInfoMapper implements Function<OidcUserInfoAuthenticationContext, OidcUserInfo> {

    private static final Log logger = LogFactory.getLog(OidcUserInfoMapper.class);
    private final UserManager userManager;
    public OidcUserInfoMapper(UserManager userManager) {
        this.userManager = userManager;
    }


    private Map<String, Object> preLoadUserInfo(OidcUserInfoAuthenticationContext authenticationContext){
        Map<String, Object> claims = authenticationContext.getAuthorization().getAccessToken().getClaims();
        User user;
        if (claims == null || claims.get("uid") == null) {
            user = userManager.findByUsername(authenticationContext.getAuthorization().getPrincipalName());
        } else {
            user = userManager.findById(claims.get("uid").toString());
        }
        // TODO: Replace with Map building instead of OidcUserInfo converting
        return OidcUserInfo.builder()
                .subject(user.getUsername())
                .name(user.getName())
                .picture(user.getPicture())
                .email(user.getEmail())
                .emailVerified(user.isEmailVerified())
                .phoneNumber(user.getPhone())
                .phoneNumberVerified(user.isPhoneVerified())
                .claim("uid", user.getId())
                .build().getClaims();
    }

    @Override
    public OidcUserInfo apply(OidcUserInfoAuthenticationContext authenticationContext) {
        Map<String, Object> userInfoClaims = preLoadUserInfo(authenticationContext);
        OAuth2AccessToken accessToken = authenticationContext.getAccessToken();
        OidcUserInfo.Builder builder = OidcUserInfo.builder()
                .claims(
                        claims-> claims.putAll(
                        getClaimsRequestedByScope(
                                userInfoClaims,
                                accessToken.getScopes()
                        )
                    )
                );

        // TODO: Add extra claims

        return builder.build();
    }

    private static Map<String, Object> getClaimsRequestedByScope(Map<String, Object> claims, Set<String> requestedScopes) {
        Set<String> scopeRequestedClaimNames = new HashSet<>(32);
        scopeRequestedClaimNames.add(StandardClaimNames.SUB);

        if (requestedScopes.contains(OidcScopes.ADDRESS)) {
            scopeRequestedClaimNames.add(StandardClaimNames.ADDRESS);
        }
        if (requestedScopes.contains(OidcScopes.EMAIL)) {
            scopeRequestedClaimNames.addAll(EMAIL_CLAIMS);
        }
        if (requestedScopes.contains(OidcScopes.PHONE)) {
            scopeRequestedClaimNames.addAll(PHONE_CLAIMS);
        }
        if (requestedScopes.contains(OidcScopes.PROFILE)) {
            scopeRequestedClaimNames.addAll(PROFILE_CLAIMS);
        }

        Map<String, Object> requestedClaims = new HashMap<>(claims);
        requestedClaims.keySet().removeIf(claimName -> !scopeRequestedClaimNames.contains(claimName));

        return requestedClaims;
    }

    private static final List<String> EMAIL_CLAIMS = Arrays.asList(
            StandardClaimNames.EMAIL,
            StandardClaimNames.EMAIL_VERIFIED
    );
    private static final List<String> PHONE_CLAIMS = Arrays.asList(
            StandardClaimNames.PHONE_NUMBER,
            StandardClaimNames.PHONE_NUMBER_VERIFIED
    );
    private static final List<String> PROFILE_CLAIMS = Arrays.asList(
            StandardClaimNames.NAME,
            StandardClaimNames.FAMILY_NAME,
            StandardClaimNames.GIVEN_NAME,
            StandardClaimNames.MIDDLE_NAME,
            StandardClaimNames.NICKNAME,
            StandardClaimNames.PREFERRED_USERNAME,
            StandardClaimNames.PROFILE,
            StandardClaimNames.PICTURE,
            StandardClaimNames.WEBSITE,
            StandardClaimNames.GENDER,
            StandardClaimNames.BIRTHDATE,
            StandardClaimNames.ZONEINFO,
            StandardClaimNames.LOCALE,
            StandardClaimNames.UPDATED_AT
    );

    // Disable never used warning
    @SuppressWarnings("unused")
    private Set<String> reflectStandardClaimNames(){
        Class<?> standardClaimNamesClass = StandardClaimNames.class;
        Field[] fields = standardClaimNamesClass.getDeclaredFields();
        Set<String> standardClaimNames = new HashSet<>();
        for (Field field : fields) {
            int modifiers = field.getModifiers();
            if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers) &&
                    field.getType().equals(String.class)) {
                try {
                    // Get the value of the field
                    String value = (String) field.get(null);
                    standardClaimNames.add(value);
                } catch (IllegalAccessException e) {
                    logger.error("Failed to get value of field " + field.getName(), e);
                }
            }
        }
        return standardClaimNames;
    }
}
