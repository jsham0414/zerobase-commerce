package com.zerobase.commerce.api.wishlist.dto;

import com.zerobase.commerce.database.wishlist.domain.Wishlist;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WishlistDto {
    private Long id;
    private String userId;
    private UUID productId;
    private Long amount;
    private LocalDateTime addedAt;

    public static WishlistDto fromEntity(Wishlist wishlist) {
        return WishlistDto.builder()
                .id(wishlist.getId())
                .userId(wishlist.getUserId())
                .productId(wishlist.getProductId())
                .amount(wishlist.getAmount())
                .addedAt(wishlist.getAddedAt())
                .build();
    }
}
