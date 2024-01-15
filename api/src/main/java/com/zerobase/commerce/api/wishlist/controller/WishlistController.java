package com.zerobase.commerce.api.wishlist.controller;

import com.zerobase.commerce.api.security.TokenAuthenticator;
import com.zerobase.commerce.api.wishlist.dto.AddWishlist;
import com.zerobase.commerce.api.wishlist.dto.UpdateWishlist;
import com.zerobase.commerce.api.wishlist.service.WishlistService;
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

@RestController
@RequiredArgsConstructor
@Tag(name = "장바구니", description = "장바구니 관리와 조회에 대한 엔드포인트를 제공합니다.")
@RequestMapping("/wishlist")
public class WishlistController {
    private final WishlistService wishlistService;
    private final TokenAuthenticator tokenAuthenticator;

    @Operation(summary = "장바구니 추가", description = "장바구니에 상품을 추가하고 반환합니다.")
    @PostMapping
    ResponseEntity<?> addWishlist(@RequestHeader HttpHeaders headers,
                                  @Validated @RequestBody AddWishlist request) {
        String userId = tokenAuthenticator.resolveTokenFromHeader(headers);
        return ResponseEntity.ok(wishlistService.addWishlist(userId, request));
    }

    @Operation(summary = "장바구니 조회", description = "본인인지 확인하고 사용자의 장바구니 리스트를 반환합니다.")
    @GetMapping
    ResponseEntity<?> getWishlist(@RequestHeader HttpHeaders headers,
                                  @PageableDefault Pageable pageable) {
        String userId = tokenAuthenticator.resolveTokenFromHeader(headers);
        return ResponseEntity.ok(wishlistService.getWishlist(userId, pageable));
    }

    @Operation(summary = "장바구니 수정", description = "본인인지 확인하고 장바구니 정보를 수정합니다.")
    @PutMapping
    ResponseEntity<?> updateWishlist(@RequestHeader HttpHeaders headers,
                                     @Validated @RequestBody UpdateWishlist request) {
        String userId = tokenAuthenticator.resolveTokenFromHeader(headers);
        return ResponseEntity.ok(wishlistService.updateWishlist(userId, request));
    }

    @Operation(summary = "장바구니 삭제", description = "본인인지 확인하고 장바구니 정보를 삭제합니다.")
    @DeleteMapping("/{wishlistId}")
    ResponseEntity<?> deleteWishlist(@RequestHeader HttpHeaders headers,
                                     @NotNull(message = "wishlistId must not be null") @PathVariable(name = "wishlistId") Long wishListId) {
        String userId = tokenAuthenticator.resolveTokenFromHeader(headers);
        wishlistService.deleteWishlist(userId, wishListId);
        return ResponseEntity.ok(null);
    }
}
