package com.zerobase.commerce.database.product.domain;

import com.zerobase.commerce.database.product.constant.ProductStatus;
import com.zerobase.commerce.database.product.constant.converter.ProductStatusConverter;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "PRODUCT")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "seller_id")
    private String sellerId;

    @Column(name = "name")
    private String name;

    @Column(name = "status")
    @Convert(converter = ProductStatusConverter.class)
    private ProductStatus status;

    @Column(name = "price")
    private Long price;

    @Column(name = "discount")
    private Double discount;

    @Column(name = "star")
    private Double star;

    @CreatedDate
    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
