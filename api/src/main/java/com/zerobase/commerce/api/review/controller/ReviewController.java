package com.zerobase.commerce.api.review.controller;

import com.zerobase.commerce.api.review.dto.ModifyReview;
import com.zerobase.commerce.api.review.dto.WriteReview;
import com.zerobase.commerce.api.review.service.ReviewService;
import com.zerobase.commerce.api.security.TokenAuthenticator;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;
    private final TokenAuthenticator tokenAuthenticator;

    @GetMapping("/{reviewId}")
    ResponseEntity<?> getReview(@NotNull(message = "reviewId must not be null") @PathVariable(name = "reviewId") Long reviewId) {
        return ResponseEntity.ok(reviewService.getReview(reviewId));
    }

    @GetMapping("/self")
    ResponseEntity<?> getSelfReviews(@RequestHeader HttpHeaders headers,
                                     @PageableDefault Pageable pageable) {
        String userId = tokenAuthenticator.resolveTokenFromHeader(headers);
        return ResponseEntity.ok(reviewService.getSelfReviews(userId, pageable));
    }

    @GetMapping("/product/{productId}")
    ResponseEntity<?> getProductReviews(@NotNull(message = "productId must not be null") @PathVariable(name = "productId") UUID productId,
                                        @PageableDefault Pageable pageable) {
        return ResponseEntity.ok(reviewService.getProductReviews(productId, pageable));
    }

    @PostMapping
    ResponseEntity<?> writeReview(@RequestHeader HttpHeaders headers,
                                  @Validated @RequestBody WriteReview request) {
        String userId = tokenAuthenticator.resolveTokenFromHeader(headers);
        return ResponseEntity.ok(reviewService.writeReview(userId, request));
    }

    @PutMapping
    ResponseEntity<?> modifyReview(@RequestHeader HttpHeaders headers,
                                   @Validated @RequestBody ModifyReview request) {
        String userId = tokenAuthenticator.resolveTokenFromHeader(headers);
        return ResponseEntity.ok(reviewService.modifyReview(userId, request));
    }

    @DeleteMapping("/{reviewId}")
    ResponseEntity<?> deleteReview(@RequestHeader HttpHeaders headers,
                                   @NotNull(message = "reviewId must not be null") @PathVariable(name = "reviewId") Long reviewId) {
        String userId = tokenAuthenticator.resolveTokenFromHeader(headers);
        reviewService.deleteReview(userId, reviewId);
        return ResponseEntity.ok(null);
    }
}
