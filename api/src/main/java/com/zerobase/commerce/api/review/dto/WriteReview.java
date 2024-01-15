package com.zerobase.commerce.api.review.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.hibernate.validator.constraints.Range;

import java.util.UUID;

@Getter
public class WriteReview {
    @NotNull(message = "Order Id must not be null")
    private UUID orderId;

    @NotBlank(message = "User Id must not be null")
    private String userId;

    @NotNull(message = "Product Id must not be null")
    private UUID productId;

    @Range(max = 5L)
    @NotNull(message = "Star must not be null")
    private Double star;
}
