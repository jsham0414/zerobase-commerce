package com.zerobase.commerce.database.order.repository;

import com.zerobase.commerce.database.order.domain.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByUserIdOrderByPurchasedAtDesc(String userId, Pageable pageable);

    List<Order> findByProductIdOrderByPurchasedAtDesc(UUID productId, Pageable pageable);
}
