package com.homework.musinsa.adapter.out.persistence.converter;


import com.homework.musinsa.domain.code.CodeValue;
import jakarta.persistence.AttributeConverter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractCodeValueConverter<E extends Enum<E> & CodeValue>
        implements AttributeConverter<E, String> {

    private final Class<E> targetEnumClass;

    @Override
    public String convertToDatabaseColumn(E attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getCode();
    }

    @Override
    public E convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return null;
        }

        return CodeValue.from(targetEnumClass, dbData);
    }
}

