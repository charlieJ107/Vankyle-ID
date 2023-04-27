package com.vankyle.id.data.utils;

import com.vankyle.id.data.entity.OAuth2TokenData;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class OAuth2TokenDataConverter implements AttributeConverter<OAuth2TokenData, String> {
    @Override
    public String convertToDatabaseColumn(OAuth2TokenData attribute) {
        if (attribute == null){
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(attribute);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public OAuth2TokenData convertToEntityAttribute(String dbData) {
        if (dbData == null){
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(dbData, OAuth2TokenData.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
