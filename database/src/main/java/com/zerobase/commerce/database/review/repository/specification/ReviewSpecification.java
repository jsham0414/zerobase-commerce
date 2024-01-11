package com.zerobase.commerce.database.review.repository.specification;

import com.zerobase.commerce.database.review.domain.Review;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class ReviewSpecification {
    public static Specification<Review> userIdEquals(String userId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("userId"), userId);
    }

    public static Specification<Review> orderIdEquals(UUID orderId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("orderId"), orderId);
    }
}
