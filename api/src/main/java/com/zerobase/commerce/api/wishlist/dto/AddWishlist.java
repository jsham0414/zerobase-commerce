package com.zerobase.commerce.api.wishlist.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddWishlist {
    @NotNull(message = "Product id must not be null")
    private UUID productId;

    @NotNull(message = "Amount must not be null")
    @Min(value = 1, message = "Amount must be more then one")
    private Long amount;
}
