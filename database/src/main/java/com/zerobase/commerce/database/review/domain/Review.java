package com.zerobase.commerce.database.review.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "REVIEW")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id")
    private UUID orderId;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "product_id")
    private UUID productId;

    @Column(name = "star")
    private Double star;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
