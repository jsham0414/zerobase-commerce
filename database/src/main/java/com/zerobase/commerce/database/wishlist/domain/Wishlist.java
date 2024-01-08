package com.zerobase.commerce.database.wishlist.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "WISHLIST")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Wishlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "product_id")
    private UUID productId;

    @Column(name = "amount")
    private Long amount;

    @Column(name = "added_at")
    private LocalDateTime addedAt;
}
