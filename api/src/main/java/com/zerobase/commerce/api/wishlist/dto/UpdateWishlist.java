package com.zerobase.commerce.api.wishlist.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateWishlist {
    @Min(value = 1, message = "Amount must be more then one")
    private Long amount;
}
