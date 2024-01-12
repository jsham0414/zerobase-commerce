package com.zerobase.commerce.api.order.controller;

import com.zerobase.commerce.api.order.service.OrderService;
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

    @GetMapping("/{id}")
    ResponseEntity<?> getOrder(@RequestHeader HttpHeaders headers,
                               @NotNull(message = "Order Id is must not be null") @PathVariable UUID id) {
        return ResponseEntity.ok(orderService.getOrder(headers, id));
    }

    @PreAuthorize("hasRole('ROLE_SELLER')")
    @PutMapping("/approve/{id}")
    ResponseEntity<?> approveOrder(@RequestHeader HttpHeaders headers,
                                   @NotNull(message = "Order Id is must not be null") @PathVariable UUID id) {
        return ResponseEntity.ok(orderService.approveOrder(headers, id));
    }

    @PutMapping("/cancel/{id}")
    ResponseEntity<?> cancelOrder(@RequestHeader HttpHeaders headers,
                                  @NotNull(message = "Order Id is must not be null") @PathVariable UUID id) {
        return ResponseEntity.ok(orderService.cancelOrder(headers, id));
    }

    @PreAuthorize("hasRole('ROLE_SELLER')")
    @PutMapping("/reject/{id}")
    ResponseEntity<?> rejectOrder(@RequestHeader HttpHeaders headers,
                                  @NotNull(message = "Order Id is must not be null") @PathVariable UUID id) {
        return ResponseEntity.ok(orderService.rejectOrder(headers, id));
    }

    @GetMapping("/self")
    ResponseEntity<?> getOrdersByUser(@RequestHeader HttpHeaders headers) {
        return ResponseEntity.ok(orderService.getOrdersByUser(headers));
    }

    @PreAuthorize("hasRole('ROLE_SELLER')")
    @GetMapping("/product/{id}")
    ResponseEntity<?> getOrdersByProduct(@RequestHeader HttpHeaders headers,
                                         @NotNull(message = "Product id must not be null") @PathVariable UUID id) {
        return ResponseEntity.ok(orderService.getOrdersByProduct(headers, id));
    }


}
