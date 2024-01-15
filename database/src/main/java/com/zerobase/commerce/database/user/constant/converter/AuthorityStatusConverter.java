package com.zerobase.commerce.database.user.constant.converter;

import com.zerobase.commerce.database.user.constant.AuthorityStatus;
import jakarta.persistence.AttributeConverter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class AuthorityStatusConverter implements AttributeConverter<Set<AuthorityStatus>, String> {
    private final String SEPARATOR = ",";

    @Override
    public String convertToDatabaseColumn(Set<AuthorityStatus> attribute) {
        return String.join(SEPARATOR, attribute.stream().map(AuthorityStatus::toString).collect(Collectors.toSet()));
    }

    @Override
    public Set<AuthorityStatus> convertToEntityAttribute(String dbData) {
        return dbData.isEmpty() ? new HashSet<>() : Arrays.stream(dbData.split(SEPARATOR)).map(AuthorityStatus::valueOf).collect(Collectors.toSet());
    }
}
