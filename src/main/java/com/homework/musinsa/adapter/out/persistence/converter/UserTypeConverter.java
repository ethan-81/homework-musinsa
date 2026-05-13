package com.homework.musinsa.adapter.out.persistence.converter;

import com.homework.musinsa.domain.code.UserType;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class UserTypeConverter extends AbstractCodeValueConverter<UserType> {
    public UserTypeConverter() {
        super(UserType.class);
    }
}

