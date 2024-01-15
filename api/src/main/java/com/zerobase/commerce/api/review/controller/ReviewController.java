package com.zerobase.commerce.api.review.controller;

import com.zerobase.commerce.api.review.dto.ModifyReview;
import com.zerobase.commerce.api.review.dto.WriteReview;
import com.zerobase.commerce.api.review.service.ReviewService;
import com.zerobase.commerce.api.security.TokenAuthenticator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "리뷰 컨트롤러", description = "리뷰 관리와 조회를 위한 엔드포인트를 제공합니다.")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;
    private final TokenAuthenticator tokenAuthenticator;

    @Operation(summary = "리뷰 상세 보기", description = "하나의 리뷰를 반환합니다.")
    @GetMapping("/{reviewId}")
    ResponseEntity<?> getReview(@NotNull(message = "reviewId must not be null") @PathVariable(name = "reviewId") Long reviewId) {
        return ResponseEntity.ok(reviewService.getReview(reviewId));
    }

    @Operation(summary = "본인 리뷰 보기", description = "본인의 리뷰들이 담긴 리스트를 반환합니다.")
    @GetMapping("/self")
    ResponseEntity<?> getSelfReviews(@RequestHeader HttpHeaders headers,
                                     @PageableDefault Pageable pageable) {
        String userId = tokenAuthenticator.resolveTokenFromHeader(headers);
        return ResponseEntity.ok(reviewService.getSelfReviews(userId, pageable));
    }

    @Operation(summary = "제품 리뷰 보기", description = "제품의 리뷰들이 담긴 리스트를 반환합니다.")
    @GetMapping("/product/{productId}")
    ResponseEntity<?> getProductReviews(@NotNull(message = "productId must not be null") @PathVariable(name = "productId") UUID productId,
                                        @PageableDefault Pageable pageable) {
        return ResponseEntity.ok(reviewService.getProductReviews(productId, pageable));
    }

    @Operation(summary = "리뷰 작성", description = "상품의 구매 내역이 있을 경우 작성하고 그 결과를 반환합니다.")
    @PostMapping
    ResponseEntity<?> writeReview(@RequestHeader HttpHeaders headers,
                                  @Validated @RequestBody WriteReview request) {
        String userId = tokenAuthenticator.resolveTokenFromHeader(headers);
        return ResponseEntity.ok(reviewService.writeReview(userId, request));
    }

    @Operation(summary = "리뷰 수정", description = "리뷰 작성자인지 확인하고 수정된 결과를 반환합니다.")
    @PutMapping
    ResponseEntity<?> modifyReview(@RequestHeader HttpHeaders headers,
                                   @Validated @RequestBody ModifyReview request) {
        String userId = tokenAuthenticator.resolveTokenFromHeader(headers);
        return ResponseEntity.ok(reviewService.modifyReview(userId, request));
    }

    @Operation(summary = "리뷰 삭제", description = "리뷰 작성자인지 확인하고 리뷰를 삭제합니다.")
    @DeleteMapping("/{reviewId}")
    ResponseEntity<?> deleteReview(@RequestHeader HttpHeaders headers,
                                   @NotNull(message = "reviewId must not be null") @PathVariable(name = "reviewId") Long reviewId) {
        String userId = tokenAuthenticator.resolveTokenFromHeader(headers);
        reviewService.deleteReview(userId, reviewId);
        return ResponseEntity.ok(null);
    }
}
