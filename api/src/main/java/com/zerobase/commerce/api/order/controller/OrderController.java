package com.zerobase.commerce.api.order.controller;

import com.zerobase.commerce.api.order.service.OrderService;
import com.zerobase.commerce.api.security.TokenAuthenticator;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final TokenAuthenticator tokenAuthenticator;

    @GetMapping("/{orderId}")
    ResponseEntity<?> getOrder(@RequestHeader HttpHeaders headers,
                               @NotNull(message = "orderId is must not be null") @PathVariable(name = "orderId") UUID orderId) {
        String userId = tokenAuthenticator.resolveTokenFromHeader(headers);
        return ResponseEntity.ok(orderService.getOrder(userId, orderId));
    }

    @PreAuthorize("hasRole('ROLE_SELLER')")
    @PutMapping("/{orderId}/approve")
    ResponseEntity<?> approveOrder(@RequestHeader HttpHeaders headers,
                                   @NotNull(message = "orderId is must not be null") @PathVariable(name = "orderId") UUID orderId) {
        String userId = tokenAuthenticator.resolveTokenFromHeader(headers);
        return ResponseEntity.ok(orderService.approveOrder(userId, orderId));
    }

    @PutMapping("{orderId}/cancel")
    ResponseEntity<?> cancelOrder(@RequestHeader HttpHeaders headers,
                                  @NotNull(message = "orderId is must not be null") @PathVariable(name = "orderId") UUID orderId) {
        String userId = tokenAuthenticator.resolveTokenFromHeader(headers);
        return ResponseEntity.ok(orderService.cancelOrder(userId, orderId));
    }

    @PreAuthorize("hasRole('ROLE_SELLER')")
    @PutMapping("/{orderId}/reject")
    ResponseEntity<?> rejectOrder(@RequestHeader HttpHeaders headers,
                                  @NotNull(message = "orderId is must not be null") @PathVariable(name = "orderId") UUID orderId) {
        String userId = tokenAuthenticator.resolveTokenFromHeader(headers);
        return ResponseEntity.ok(orderService.rejectOrder(userId, orderId));
    }

    @GetMapping("/self")
    ResponseEntity<?> getOrdersByUser(@RequestHeader HttpHeaders headers) {
        String userId = tokenAuthenticator.resolveTokenFromHeader(headers);
        return ResponseEntity.ok(orderService.getOrdersByUser(userId));
    }

    @PreAuthorize("hasRole('ROLE_SELLER')")
    @GetMapping("/product/{productId}")
    ResponseEntity<?> getOrdersByProduct(@RequestHeader HttpHeaders headers,
                                         @NotNull(message = "productId must not be null") @PathVariable(name = "productId") UUID productId) {
        String userId = tokenAuthenticator.resolveTokenFromHeader(headers);
        return ResponseEntity.ok(orderService.getOrdersByProduct(userId, productId));
    }

    @PostMapping
    ResponseEntity<?> purchase(@RequestHeader HttpHeaders headers) {
        String userId = tokenAuthenticator.resolveTokenFromHeader(headers);
        return ResponseEntity.ok(orderService.purchase(userId));
    }

}
