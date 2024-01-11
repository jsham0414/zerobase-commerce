package com.zerobase.commerce.api.review.controller;

import com.zerobase.commerce.api.review.dto.ModifyReview;
import com.zerobase.commerce.api.review.dto.WriteReview;
import com.zerobase.commerce.api.review.service.ReviewService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
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

    @GetMapping("/{id}")
    ResponseEntity<?> getReview(@NotNull(message = "Id must not be null") @PathVariable Long id) {
        return ResponseEntity.ok(reviewService.getReview(id));
    }

    @GetMapping("/self")
    ResponseEntity<?> getSelfReviews(@RequestHeader HttpHeaders headers) {
        return ResponseEntity.ok(reviewService.getSelfReviews(headers));
    }

    @GetMapping("/product/{id}")
    ResponseEntity<?> getProductReviews(@NotNull(message = "Product Id must not be null") @PathVariable UUID productId) {
        return ResponseEntity.ok(reviewService.getProductReviews(productId));
    }

    @PostMapping
    ResponseEntity<?> writeReview(@RequestHeader HttpHeaders headers,
                                  @Validated @RequestBody WriteReview request) {
        return ResponseEntity.ok(reviewService.writeReview(headers, request));
    }

    @PutMapping
    ResponseEntity<?> modifyReview(@RequestHeader HttpHeaders headers,
                                   @Validated @RequestBody ModifyReview request) {
        return ResponseEntity.ok(reviewService.modifyReview(headers, request));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteReview(@RequestHeader HttpHeaders headers,
                                   @NotNull(message = "id must not be null") @PathVariable Long id) {
        reviewService.deleteReview(headers, id);
        return ResponseEntity.ok(null);
    }
}
