package com.homework.musinsa.adapter.out.persistence.converter;

import com.homework.musinsa.domain.code.TransactionStatus;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class TransactionStatusConverter extends AbstractCodeValueConverter<TransactionStatus> {
    public TransactionStatusConverter() {
        super(TransactionStatus.class);
    }
}
