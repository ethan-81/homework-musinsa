package com.homework.musinsa.adapter.out.persistence.converter;

import com.homework.musinsa.domain.code.ProcessingType;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class ProcessingTypeConverter extends AbstractCodeValueConverter<ProcessingType> {
    public ProcessingTypeConverter() {
        super(ProcessingType.class);
    }
}
