package com.homework.musinsa.adapter.out.persistence.converter;

import com.homework.musinsa.domain.code.ProcessingCause;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class ProcessingCauseConverter extends AbstractCodeValueConverter<ProcessingCause> {
    public ProcessingCauseConverter() {
        super(ProcessingCause.class);
    }
}
