package com.vankyle.id.data.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Converter
public class AuthorityConverter implements AttributeConverter<GrantedAuthority, String> {
    @Override
    public String convertToDatabaseColumn(GrantedAuthority attribute) {
        return attribute.getAuthority();
    }

    @Override
    public GrantedAuthority convertToEntityAttribute(String dbData) {
        return new SimpleGrantedAuthority(dbData);
    }
}
