package com.zerobase.commerce.api.product.controller;

import com.zerobase.commerce.api.product.dto.AddProduct;
import com.zerobase.commerce.api.product.dto.SearchProductByName;
import com.zerobase.commerce.api.product.dto.UpdateProduct;
import com.zerobase.commerce.api.product.service.ProductService;
import com.zerobase.commerce.api.security.TokenAuthenticator;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final TokenAuthenticator tokenAuthenticator;

    @PreAuthorize("hasRole('ROLE_SELLER')")
    @PostMapping
    ResponseEntity<?> addProduct(@RequestHeader HttpHeaders headers,
                                 @Validated @RequestBody AddProduct request) {
        String userId = tokenAuthenticator.resolveTokenFromHeader(headers);
        return ResponseEntity.ok(productService.addProduct(userId, request));
    }

    @GetMapping("/{productId}")
    ResponseEntity<?> getProduct(
            @NotNull(message = "productId must not be blank") @PathVariable(name = "productId") UUID productId) {
        return ResponseEntity.ok(productService.getProduct(productId));
    }

    @PreAuthorize("hasRole('ROLE_SELLER')")
    @PutMapping
    ResponseEntity<?> updateProduct(@RequestHeader HttpHeaders headers,
                                    @Validated @RequestBody UpdateProduct request) {
        String userId = tokenAuthenticator.resolveTokenFromHeader(headers);
        return ResponseEntity.ok(productService.updateProduct(userId, request));
    }

    @PreAuthorize("hasRole('ROLE_SELLER')")
    @DeleteMapping("/{productId}")
    ResponseEntity<?> deleteProduct(@RequestHeader HttpHeaders headers,
                                    @NotNull(message = "Id is must not be null") @PathVariable(name = "productId") UUID productId) {
        String userId = tokenAuthenticator.resolveTokenFromHeader(headers);
        productService.deleteProduct(userId, productId);
        return ResponseEntity.ok(null);
    }

    @PreAuthorize("hasRole('ROLE_SELLER')")
    @GetMapping("/seller")
    ResponseEntity<?> getProductsBySeller(@RequestHeader HttpHeaders headers,
                                          @PageableDefault Pageable pageable) {
        String userId = tokenAuthenticator.resolveTokenFromHeader(headers);
        return ResponseEntity.ok(productService.getProductsBySeller(userId, pageable));
    }

    @GetMapping
    ResponseEntity<?> getProducts(@RequestBody SearchProductByName searchProductByName,
                                  @PageableDefault Pageable pageable) {
        return ResponseEntity.ok(productService.getProductsByName(searchProductByName, pageable));
    }

    @GetMapping("/ranking")
    ResponseEntity<?> getStarRanking() {
        return ResponseEntity.ok(productService.getStarRanking());
    }

}
