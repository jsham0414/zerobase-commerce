package com.zerobase.commerce.api.wishlist.controller;

import com.zerobase.commerce.api.security.TokenAuthenticator;
import com.zerobase.commerce.api.wishlist.dto.AddWishlist;
import com.zerobase.commerce.api.wishlist.dto.UpdateWishlist;
import com.zerobase.commerce.api.wishlist.service.WishlistService;
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
@RequestMapping("/wishlist")
public class WishlistController {
    private final WishlistService wishlistService;
    private final TokenAuthenticator tokenAuthenticator;

    @PostMapping
    ResponseEntity<?> addWishlist(@RequestHeader HttpHeaders headers,
                                  @Validated @RequestBody AddWishlist request) {
        String userId = tokenAuthenticator.resolveTokenFromHeader(headers);
        return ResponseEntity.ok(wishlistService.addWishlist(userId, request));
    }

    @GetMapping
    ResponseEntity<?> getWishlist(@RequestHeader HttpHeaders headers,
                                  @PageableDefault Pageable pageable) {
        String userId = tokenAuthenticator.resolveTokenFromHeader(headers);
        return ResponseEntity.ok(wishlistService.getWishlist(userId, pageable));
    }

    @PutMapping
    ResponseEntity<?> updateWishlist(@RequestHeader HttpHeaders headers,
                                     @Validated @RequestBody UpdateWishlist request) {
        String userId = tokenAuthenticator.resolveTokenFromHeader(headers);
        return ResponseEntity.ok(wishlistService.updateWishlist(userId, request));
    }

    @DeleteMapping("/{wishlistId}")
    ResponseEntity<?> deleteWishlist(@RequestHeader HttpHeaders headers,
                                     @NotNull(message = "wishlistId must not be null") @PathVariable(name = "wishlistId") Long wishListId) {
        String userId = tokenAuthenticator.resolveTokenFromHeader(headers);
        wishlistService.deleteWishlist(userId, wishListId);
        return ResponseEntity.ok(null);
    }
}
