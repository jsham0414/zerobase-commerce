package com.zerobase.commerce.api.product.controller;

import com.zerobase.commerce.api.product.dto.AddProduct;
import com.zerobase.commerce.api.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PreAuthorize("hasRole('ROLE_SELLER')")
    @PostMapping
    ResponseEntity<?> addProduct(@RequestHeader HttpHeaders headers, @Validated @RequestBody AddProduct.Request request) {
        return ResponseEntity.ok(productService.addProduct(headers, request));
    }
}
