package com.homework.musinsa.adapter.out.persistence.converter;

import com.homework.musinsa.domain.code.TransactionType;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class TransactionTypeConverter extends AbstractCodeValueConverter<TransactionType> {
    public TransactionTypeConverter() {
        super(TransactionType.class);
    }
}

