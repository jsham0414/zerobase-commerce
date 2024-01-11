package com.zerobase.commerce.api.review.service;

import com.zerobase.commerce.api.exception.CustomException;
import com.zerobase.commerce.api.exception.ErrorCode;
import com.zerobase.commerce.api.product.dto.ProductDto;
import com.zerobase.commerce.api.review.dto.ModifyReview;
import com.zerobase.commerce.api.review.dto.ReviewDto;
import com.zerobase.commerce.api.review.dto.WriteReview;
import com.zerobase.commerce.api.security.TokenAuthenticator;
import com.zerobase.commerce.database.product.domain.Product;
import com.zerobase.commerce.database.product.repository.ProductRepository;
import com.zerobase.commerce.database.review.domain.Review;
import com.zerobase.commerce.database.review.repository.ReviewRepository;
import com.zerobase.commerce.database.review.repository.specification.ReviewSpecification;
import com.zerobase.commerce.database.user.domain.User;
import com.zerobase.commerce.database.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final TokenAuthenticator tokenAuthenticator;
    private final ProductRepository productRepository;

    public ReviewDto getReview(Long id) {
        Review review = reviewRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.INVALID_REVIEW_ID)
        );

        return ReviewDto.fromEntity(review);
    }

    public List<ReviewDto> getSelfReviews(HttpHeaders headers) {
        String userId = tokenAuthenticator.resolveTokenFromHeader(headers);
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.INVALID_USER_ID)
        );

        return reviewRepository.findByUserId(user.getId())
                .stream()
                .map(ReviewDto::fromEntity)
                .toList();
    }

    public List<ReviewDto> getProductReviews(UUID productId) {
        if (!productRepository.existsById(productId)) {
            throw new CustomException(ErrorCode.INVALID_PRODUCT_ID);
        }

        return reviewRepository.findByProductId(productId)
                .stream()
                .map(ReviewDto::fromEntity)
                .toList();
    }

    @Transactional
    public ReviewDto writeReview(HttpHeaders headers, WriteReview request) {
        String userId = tokenAuthenticator.resolveTokenFromHeader(headers);

        Specification<Review> specification = Specification
                .where(ReviewSpecification.userIdEquals(userId))
                .and(ReviewSpecification.orderIdEquals(request.getOrderId()));

        if (reviewRepository.exists(specification)) {
            throw new CustomException(ErrorCode.WRITTEN_REVIEW);
        }

        LocalDateTime now = LocalDateTime.now();

        Review review = Review.builder()
                .orderId(request.getOrderId())
                .userId(userId)
                .productId(request.getProductId())
                .star(request.getStar())
                .reviewedAt(now)
                .updatedAt(now)
                .build();

        return ReviewDto.fromEntity(reviewRepository.save(review));
    }

    @Transactional
    public ReviewDto modifyReview(HttpHeaders headers, ModifyReview request) {
        String userId = tokenAuthenticator.resolveTokenFromHeader(headers);
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.INVALID_USER_ID)
        );

        Review review = reviewRepository.findById(request.getId()).orElseThrow(
                () -> new CustomException(ErrorCode.INVALID_REVIEW_ID)
        );

        if (!Objects.equals(user.getId(), review.getUserId())) {
            throw new CustomException(ErrorCode.USER_ID_NOT_SAME);
        }

        if (request.getStar() != null) {
            review.setStar(request.getStar());
        }

        return ReviewDto.fromEntity(review);
    }

    @Transactional
    public void deleteReview(HttpHeaders headers, Long id) {
        String userId = tokenAuthenticator.resolveTokenFromHeader(headers);
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.INVALID_USER_ID)
        );

        Review review = reviewRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.INVALID_REVIEW_ID)
        );

        if (!Objects.equals(user.getId(), review.getUserId())) {
            throw new CustomException(ErrorCode.USER_ID_NOT_SAME);
        }

        reviewRepository.delete(review);
    }
}
