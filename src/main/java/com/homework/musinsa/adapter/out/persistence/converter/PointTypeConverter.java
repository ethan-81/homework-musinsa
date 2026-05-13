package com.homework.musinsa.adapter.out.persistence.converter;

import com.homework.musinsa.domain.code.PointType;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class PointTypeConverter extends AbstractCodeValueConverter<PointType> {
    public PointTypeConverter() {
        super(PointType.class);
    }
}
