package com.zerobase.commerce.database.constant.converter;

import com.zerobase.commerce.database.constant.ProductStatus;
import jakarta.persistence.AttributeConverter;

public class ProductStatusConverter implements AttributeConverter<ProductStatus, String> {
    @Override
    public String convertToDatabaseColumn(ProductStatus attribute) {
        return attribute.name();
    }

    @Override
    public ProductStatus convertToEntityAttribute(String dbData) {
        return ProductStatus.valueOf(dbData);
    }
}
