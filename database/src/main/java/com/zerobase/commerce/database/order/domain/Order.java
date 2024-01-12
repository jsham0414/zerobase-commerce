package com.zerobase.commerce.database.order.domain;

import com.zerobase.commerce.database.order.constant.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ORDER_INFO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "product_id")
    private UUID productId;

    @Column(name = "amount")
    private Long amount;

    @Column(name = "price")
    private Long price;

    @Column(name = "discount")
    private Double discount;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @CreatedDate
    @Column(name = "purchased_at")
    private LocalDateTime purchasedAt;
}
