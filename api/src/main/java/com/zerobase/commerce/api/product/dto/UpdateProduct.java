package com.zerobase.commerce.api.product.dto;

import com.zerobase.commerce.api.validation.EnumCheck;
import com.zerobase.commerce.database.product.constant.ProductStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Range;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateProduct {
    @NotNull(message = "Id is must not be null")
    private UUID id;

    @NotBlank(message = "SellerId is must not be blank")
    private String sellerId;

    private String name;

    @EnumCheck(check = ProductStatus.class, message = "Status is invalid")
    private String status;

    @Range(min = 0L, message = "Discount must be at least zero")
    private Long price;

    @Range(min = 0, max = 100, message = "Discount must be 0 to 100")
    private Double discount;
}