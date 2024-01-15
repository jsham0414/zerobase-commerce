package com.zerobase.commerce.api.product.dto;

import com.zerobase.commerce.api.validation.EnumCheck;
import com.zerobase.commerce.database.product.constant.ProductSortFilter;
import com.zerobase.commerce.database.product.constant.SortOrder;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchProductByName {
    @NotBlank(message = "Name is must not be blank")
    String name;

    @EnumCheck(check = ProductSortFilter.class, message = "")
    String filter;

    @EnumCheck(check = SortOrder.class, message = "")
    String order;
}
