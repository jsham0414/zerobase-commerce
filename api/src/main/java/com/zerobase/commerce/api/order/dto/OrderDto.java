package com.zerobase.commerce.api.order.dto;

import com.zerobase.commerce.database.order.constant.OrderStatus;
import com.zerobase.commerce.database.order.domain.Order;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDto {
    private UUID id;

    private String userId;

    private UUID productId;

    private Long amount;

    private Long price;

    private Double discount;

    private OrderStatus status;

    private LocalDateTime purchasedAt;

    public static OrderDto fromEntity(Order order) {
        return OrderDto.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .productId(order.getProductId())
                .amount(order.getAmount())
                .discount(order.getDiscount())
                .price(order.getPrice())
                .status(order.getStatus())
                .purchasedAt(order.getPurchasedAt())
                .build();
    }
}
