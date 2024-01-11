package com.zerobase.commerce.api.wishlist.controller;

import com.zerobase.commerce.api.wishlist.dto.AddWishlist;
import com.zerobase.commerce.api.wishlist.dto.UpdateWishlist;
import com.zerobase.commerce.api.wishlist.service.WishlistService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/wishlist")
public class WishlistController {
    private final WishlistService wishlistService;

    @PostMapping
    ResponseEntity<?> addWishlist(@RequestHeader HttpHeaders headers,
                                  @Validated @RequestBody AddWishlist request) {
        return ResponseEntity.ok(wishlistService.addWishlist(headers, request));
    }

    @GetMapping
    ResponseEntity<?> getWishlist(@RequestHeader HttpHeaders headers) {
        return ResponseEntity.ok(wishlistService.getWishlist(headers));
    }

    @PutMapping("/{id}")
    ResponseEntity<?> updateWishlist(@RequestHeader HttpHeaders headers,
                                     @NotNull(message = "Wishlist Id must not be null") @PathVariable(name = "id") Long id,
                                     @Validated @RequestBody UpdateWishlist request) {
        return ResponseEntity.ok(wishlistService.updateWishlist(headers, id, request));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteWishlist(@RequestHeader HttpHeaders headers,
                                     @NotNull(message = "Wishlist Id must not be null") @PathVariable(name = "id") Long id) {
        wishlistService.deleteWishlist(headers, id);
        return ResponseEntity.ok(null);
    }
}
