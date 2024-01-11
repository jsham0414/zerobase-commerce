package com.zerobase.commerce.api.wishlist.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateWishlist {
    @Min(value = 1, message = "Amount must be more then one")
    private Long amount;
}
