package com.vankyle.id.data.utils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode;

@Converter
public class OAuth2TokenTypeConverter implements AttributeConverter<Class<? extends OAuth2Token>, String> {
    @Override
    public String convertToDatabaseColumn(Class<? extends OAuth2Token> attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getName();
    }

    @Override
    public Class<? extends OAuth2Token> convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        if (dbData.equals(OAuth2AuthorizationCode.class.getName())) {
            return OAuth2AuthorizationCode.class;
        } else if (dbData.equals(OAuth2AccessToken.class.getName())) {
            return OAuth2AccessToken.class;
        } else if (dbData.equals(OAuth2RefreshToken.class.getName())) {
            return OAuth2RefreshToken.class;
        } else if (dbData.equals(Jwt.class.getName())) {
            return Jwt.class;
        } else if (dbData.equals(OidcIdToken.class.getName())) {
            return OidcIdToken.class;
        } else if (dbData.equals(OAuth2ParameterNames.USER_CODE)) {
            return OAuth2UserCode.class;
        } else if (dbData.equals(OAuth2ParameterNames.DEVICE_CODE)) {
            return OAuth2DeviceCode.class;
        } else {
            return AbstractOAuth2Token.class;
        }
    }

}
