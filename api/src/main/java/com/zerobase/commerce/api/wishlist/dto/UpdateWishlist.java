package com.zerobase.commerce.api.wishlist.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateWishlist {
    @NotNull(message = "wishlistId must not be null")
    private Long wishlistId;

    @Min(value = 1, message = "Amount must be more then one")
    private Long amount;
}
