package com.zerobase.commerce.api.product.controller;

import com.zerobase.commerce.api.product.dto.AddProduct;
import com.zerobase.commerce.api.product.dto.UpdateProduct;
import com.zerobase.commerce.api.product.service.ProductService;
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

    @PreAuthorize("hasRole('ROLE_SELLER')")
    @PostMapping
    ResponseEntity<?> addProduct(
            @RequestHeader HttpHeaders headers, @Validated @RequestBody AddProduct request) {
        return ResponseEntity.ok(productService.addProduct(headers, request));
    }

    @GetMapping("/{productId}")
    ResponseEntity<?> getProduct(
            @NotNull(message = "id must not be blank") @PathVariable UUID productId) {
        return ResponseEntity.ok(productService.getProduct(productId));
    }

    @PreAuthorize("hasRole('ROLE_SELLER')")
    @PutMapping
    ResponseEntity<?> updateProduct(
            @RequestHeader HttpHeaders headers, @Validated @RequestBody UpdateProduct request) {
        return ResponseEntity.ok(productService.updateProduct(headers, request));
    }

    @PreAuthorize("hasRole('ROLE_SELLER')")
    @DeleteMapping
    ResponseEntity<?> deleteProduct(
            @RequestHeader HttpHeaders headers, @NotNull(message = "Id is must not be null") @RequestBody UUID productId) {
        productService.deleteProduct(headers, productId);
        return ResponseEntity.ok(null);
    }

    @PreAuthorize("hasRole('ROLE_SELLER')")
    @GetMapping("/products")
    ResponseEntity<?> getProductsBySeller(
            @RequestHeader HttpHeaders headers) {
        return ResponseEntity.ok(productService.getProductsBySeller(headers));
    }

    @GetMapping
    ResponseEntity<?> getProducts(
            @NotBlank(message = "Name is must not be blank") @RequestParam String name,
            @EnumCheck(check = ProductSortFilter.class, message = "") @RequestParam(required = false) String filter,
            @EnumCheck(check = SortOrder.class, message = "") @RequestParam(required = false) String order) {
        return ResponseEntity.ok(productService.getProductsByName(name, filter, order));
    }

}
