package com.homework.musinsa.adapter.out.persistence.converter;

import com.homework.musinsa.domain.code.TransactionEventType;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class TransactionEventTypeConverter
        extends AbstractCodeValueConverter<TransactionEventType> {
    public TransactionEventTypeConverter() {
        super(TransactionEventType.class);
    }
}
