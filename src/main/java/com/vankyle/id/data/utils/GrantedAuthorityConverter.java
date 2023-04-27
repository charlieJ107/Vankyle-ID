package com.vankyle.id.data.utils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Converter
public class GrantedAuthorityConverter implements AttributeConverter<GrantedAuthority, String>{
    @Override
    public String convertToDatabaseColumn(GrantedAuthority attribute) {
        if (attribute == null){
            return null;
        }
        return attribute.getAuthority();
    }

    @Override
    public GrantedAuthority convertToEntityAttribute(String dbData) {
        if (dbData == null){
            return null;
        }
        return new SimpleGrantedAuthority(dbData);
    }
}
