package com.zerobase.commerce.database.wishlist.repository;

import com.zerobase.commerce.database.wishlist.domain.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    List<Wishlist> findByUserIdOrderByAddedAtDesc(String userId);
    List<Wishlist> findByUserId(String userId);
    void deleteAllByUserId(String userId);
}
