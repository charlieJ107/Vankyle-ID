package com.vankyle.id.data.utils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.security.oauth2.jose.jws.JwsAlgorithm;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;

@Converter
public class JwsAlgorithmConverter implements AttributeConverter<JwsAlgorithm, String> {
    @Override
    public String convertToDatabaseColumn(JwsAlgorithm attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getName();
    }

    @Override
    public JwsAlgorithm convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return SignatureAlgorithm.from(dbData);
    }
}
