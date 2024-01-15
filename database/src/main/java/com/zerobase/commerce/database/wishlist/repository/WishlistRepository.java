package com.zerobase.commerce.database.wishlist.repository;

import com.zerobase.commerce.database.wishlist.domain.Wishlist;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    List<Wishlist> findByUserIdOrderByAddedAtDesc(String userId, Pageable pageable);

    List<Wishlist> findByUserId(String userId, Pageable pageable);

    void deleteAllByUserId(String userId);

    void deleteAllByProductId(UUID productId);
}
