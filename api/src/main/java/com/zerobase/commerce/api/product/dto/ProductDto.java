package com.zerobase.commerce.api.product.dto;

import com.zerobase.commerce.database.constant.ProductStatus;
import com.zerobase.commerce.database.domain.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;


@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDto {
    private UUID id;
    private String sellerId;
    private String name;
    private ProductStatus status;
    private Long price;
    private Double discount;
    private Double star;
    private LocalDateTime publishedAt;
    private LocalDateTime updatedAt;

    public static ProductDto fromEntity(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .sellerId(product.getSellerId())
                .name(product.getName())
                .status(product.getStatus())
                .price(product.getPrice())
                .discount(product.getDiscount())
                .star(product.getStar())
                .publishedAt(product.getPublishedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}