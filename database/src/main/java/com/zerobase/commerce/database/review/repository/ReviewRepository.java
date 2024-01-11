package com.zerobase.commerce.database.review.repository;

import com.zerobase.commerce.database.review.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, Long>, JpaSpecificationExecutor<Review> {
    List<Review> findByProductId(UUID productId);
    List<Review> findByUserId(String userId);
}
