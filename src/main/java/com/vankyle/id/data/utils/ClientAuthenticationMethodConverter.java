package com.vankyle.id.data.utils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

@Converter
public class ClientAuthenticationMethodConverter implements AttributeConverter<ClientAuthenticationMethod, String> {
    @Override
    public String convertToDatabaseColumn(ClientAuthenticationMethod attribute) {
        if (attribute == null){
            return null;
        }
        return attribute.getValue();
    }

    @Override
    public ClientAuthenticationMethod convertToEntityAttribute(String dbData) {
        if (dbData == null){
            return null;
        }
        if (ClientAuthenticationMethod.CLIENT_SECRET_BASIC.getValue().equals(dbData)) {
            return ClientAuthenticationMethod.CLIENT_SECRET_BASIC;
        } else if (ClientAuthenticationMethod.CLIENT_SECRET_POST.getValue().equals(dbData)) {
            return ClientAuthenticationMethod.CLIENT_SECRET_POST;
        } else if (ClientAuthenticationMethod.NONE.getValue().equals(dbData)) {
            return ClientAuthenticationMethod.NONE;
        }
        return new ClientAuthenticationMethod(dbData);        // Custom client authentication method
    }
}
