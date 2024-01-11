package com.zerobase.commerce.api.review.dto;

import com.zerobase.commerce.database.review.domain.Review;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDto {
    private Long id;

    private UUID orderId;

    private String userId;

    private UUID productId;

    private Double star;

    private LocalDateTime reviewedAt;

    private LocalDateTime updatedAt;

    public static ReviewDto fromEntity(Review review) {
        return ReviewDto.builder()
                .id(review.getId())
                .orderId(review.getOrderId())
                .userId(review.getUserId())
                .productId(review.getProductId())
                .star(review.getStar())
                .reviewedAt(review.getReviewedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}
