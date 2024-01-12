package com.zerobase.commerce.api.product.controller;

import com.zerobase.commerce.api.product.dto.AddProduct;
import com.zerobase.commerce.api.product.dto.UpdateProduct;
import com.zerobase.commerce.api.product.service.ProductService;
import com.zerobase.commerce.api.security.TokenAuthenticator;
import com.zerobase.commerce.api.validation.EnumCheck;
import com.zerobase.commerce.database.product.constant.ProductSortFilter;
import com.zerobase.commerce.database.product.constant.SortOrder;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
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
    @DeleteMapping
    ResponseEntity<?> deleteProduct(@RequestHeader HttpHeaders headers,
                                    @NotNull(message = "Id is must not be null") @RequestBody UUID productId) {
        String userId = tokenAuthenticator.resolveTokenFromHeader(headers);
        productService.deleteProduct(userId, productId);
        return ResponseEntity.ok(null);
    }

    @PreAuthorize("hasRole('ROLE_SELLER')")
    @GetMapping("/seller")
    ResponseEntity<?> getProductsBySeller(
            @RequestHeader HttpHeaders headers) {
        String userId = tokenAuthenticator.resolveTokenFromHeader(headers);
        return ResponseEntity.ok(productService.getProductsBySeller(userId));
    }

    @GetMapping
    ResponseEntity<?> getProducts(@NotBlank(message = "Name is must not be blank") @RequestParam String name,
                                  @EnumCheck(check = ProductSortFilter.class, message = "") @RequestParam(required = false) String filter,
                                  @EnumCheck(check = SortOrder.class, message = "") @RequestParam(required = false) String order) {
        return ResponseEntity.ok(productService.getProductsByName(name, filter, order));
    }

    @GetMapping("/ranking")
    ResponseEntity<?> getStarRanking() {
        return ResponseEntity.ok(productService.getStarRanking());
    }

}
