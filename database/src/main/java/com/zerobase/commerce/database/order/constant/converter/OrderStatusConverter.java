package com.zerobase.commerce.database.order.constant.converter;

import com.zerobase.commerce.database.order.constant.OrderStatus;
import com.zerobase.commerce.database.product.constant.ProductStatus;
import jakarta.persistence.AttributeConverter;

public class OrderStatusConverter implements AttributeConverter<OrderStatus, String> {
    @Override
    public String convertToDatabaseColumn(OrderStatus attribute) {
        return attribute.name();
    }

    @Override
    public OrderStatus convertToEntityAttribute(String dbData) {
        return OrderStatus.valueOf(dbData);
    }
}
