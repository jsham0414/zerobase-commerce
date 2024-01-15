package com.zerobase.commerce.api.product.dto;

import com.zerobase.commerce.api.validation.EnumCheck;
import com.zerobase.commerce.database.product.constant.ProductStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;


@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddProduct {
    @NotBlank(message = "Name must not be blank")
    private String name;

    @NotNull(message = "Price must not be blank")
    @Range(min = 0, message = "Price must be greater than zero")
    private Long price;

    @EnumCheck(check = ProductStatus.class, message = "Status is invalid")
    private String status;
}
