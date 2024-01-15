package com.zerobase.commerce.api.review.service;

import com.zerobase.commerce.api.review.dto.ModifyReview;
import com.zerobase.commerce.api.review.dto.ReviewDto;
import com.zerobase.commerce.api.review.dto.WriteReview;
import com.zerobase.commerce.database.product.repository.ProductRepository;
import com.zerobase.commerce.database.review.domain.Review;
import com.zerobase.commerce.database.review.repository.ReviewRepository;
import com.zerobase.commerce.database.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ReviewService reviewService;

    private Review review;

    @BeforeEach
    void setUp() {
        review = Review.builder()
                .id(1L)
                .userId("test")
                .orderId(UUID.randomUUID())
                .productId(UUID.randomUUID())
                .star(4.2)
                .updatedAt(LocalDateTime.now())
                .reviewedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void getReview() {
        given(reviewRepository.findById(anyLong()))
                .willReturn(Optional.of(review));

        ReviewDto reviewDto = reviewService.getReview(review.getId());

        verify(reviewRepository, times(1)).findById(review.getId());

        assertAll(
                () -> assertEquals(reviewDto.getId(), review.getId()),
                () -> assertEquals(reviewDto.getProductId(), review.getProductId()),
                () -> assertEquals(reviewDto.getOrderId(), review.getOrderId()),
                () -> assertEquals(reviewDto.getUserId(), review.getUserId()),
                () -> assertEquals(reviewDto.getStar(), review.getStar()),
                () -> assertEquals(reviewDto.getUpdatedAt(), review.getUpdatedAt())
        );
    }

    @Test
    void getSelfReviews() {
        Review anotherReview = Review.builder()
                .id(2L)
                .productId(UUID.randomUUID())
                .star(3.2)
                .userId("test")
                .build();

        given(productRepository.existsById(any()))
                .willReturn(true);
        given(reviewRepository.findByUserId(any(), any()))
                .willReturn(List.of(review, anotherReview));

        List<ReviewDto> reviews = reviewService.getProductReviews(review.getProductId(), Pageable.unpaged());

        verify(reviewRepository, times(1)).findByUserId(review.getUserId(), Pageable.unpaged());

        assertEquals(reviews.size(), 2);
    }

    @Test
    void getProductReviews() {
        Review anotherReview = Review.builder()
                .id(2L)
                .productId(review.getProductId())
                .star(3.2)
                .userId("anotherUser")
                .build();

        given(productRepository.existsById(any()))
                .willReturn(true);
        given(reviewRepository.findByProductId(any(), any()))
                .willReturn(List.of(review, anotherReview));

        List<ReviewDto> reviews = reviewService.getProductReviews(review.getProductId(), Pageable.unpaged());

        assertEquals(reviews.size(), 2);
    }

    @Test
    void writeReview() {
        WriteReview writeReview = WriteReview.builder()
                .userId("test")
                .productId(review.getProductId())
                .orderId(review.getOrderId())
                .star(4.2)
                .build();

        given(reviewRepository.exists(any(Specification.class)))
                .willReturn(false);
        given(reviewRepository.save(any()))
                .willReturn(review);

        ArgumentCaptor<Review> captor = ArgumentCaptor.forClass(Review.class);

        ReviewDto reviewDto = reviewService.writeReview("test", writeReview);

        verify(reviewRepository, times(1)).save(captor.capture());

        assertAll(
                () -> assertEquals(reviewDto.getOrderId(), writeReview.getOrderId()),
                () -> assertEquals(reviewDto.getProductId(), writeReview.getProductId()),
                () -> assertEquals(reviewDto.getStar(), writeReview.getStar()),
                () -> assertEquals(reviewDto.getUserId(), writeReview.getUserId())
        );
    }

    @Test
    void modifyReview() {
        ModifyReview modifyReview = ModifyReview.builder()
                .id(review.getId())
                .star(4.4)
                .build();

        given(reviewRepository.findById(anyLong()))
                .willReturn(Optional.of(review));

        ReviewDto reviewDto = reviewService.modifyReview("test", modifyReview);

        verify(reviewRepository, times(1)).findById(modifyReview.getId());

        assertAll(
                () -> assertEquals(reviewDto.getId(), modifyReview.getId()),
                () -> assertEquals(reviewDto.getStar(), modifyReview.getStar())
        );
    }

    @Test
    void deleteReview() {
        given(reviewRepository.findById(anyLong()))
                .willReturn(Optional.of(review));

        reviewService.deleteReview("test", review.getId());
    }
}