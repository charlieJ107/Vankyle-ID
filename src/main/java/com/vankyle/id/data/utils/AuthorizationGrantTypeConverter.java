package com.vankyle.id.data.utils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

@Converter
public class AuthorizationGrantTypeConverter implements AttributeConverter<AuthorizationGrantType, String> {
    @Override
    public String convertToDatabaseColumn(AuthorizationGrantType attribute) {
        if (attribute == null){
            return null;
        }
        return attribute.getValue();
    }

    @Override
    public AuthorizationGrantType convertToEntityAttribute(String dbData) {
        if (dbData == null){
            return null;
        }
        if (AuthorizationGrantType.AUTHORIZATION_CODE.getValue().equals(dbData)) {
            return AuthorizationGrantType.AUTHORIZATION_CODE;
        } else if (AuthorizationGrantType.CLIENT_CREDENTIALS.getValue().equals(dbData)) {
            return AuthorizationGrantType.CLIENT_CREDENTIALS;
        } else if (AuthorizationGrantType.REFRESH_TOKEN.getValue().equals(dbData)) {
            return AuthorizationGrantType.REFRESH_TOKEN;
        }
        return new AuthorizationGrantType(dbData);        // Custom authorization grant type
    }
}
