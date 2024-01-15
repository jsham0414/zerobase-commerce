package com.zerobase.commerce.api.order.controller;

import com.zerobase.commerce.api.order.service.OrderService;
import com.zerobase.commerce.api.security.TokenAuthenticator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/order")
@Tag(name = "주문 컨트롤러", description = "주문 관리와 조회에 대한 엔드포인트들을 제공합니다.")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final TokenAuthenticator tokenAuthenticator;

    @Operation(summary = "주문 조회", description = "본인 확인 후 아이디와 같은 주문 정보를 반환합니다.")
    @GetMapping("/{orderId}")
    ResponseEntity<?> getOrder(@RequestHeader HttpHeaders headers,
                               @NotNull(message = "orderId is must not be null") @PathVariable(name = "orderId") UUID orderId) {
        String userId = tokenAuthenticator.resolveTokenFromHeader(headers);
        return ResponseEntity.ok(orderService.getOrder(userId, orderId));
    }

    @Operation(summary = "주문 승인", description = "판매자 확인 후 주문이 대기 상태라면 주문을 승인합니다.")
    @PreAuthorize("hasRole('ROLE_SELLER')")
    @PutMapping("/{orderId}/approve")
    ResponseEntity<?> approveOrder(@RequestHeader HttpHeaders headers,
                                   @NotNull(message = "orderId is must not be null") @PathVariable(name = "orderId") UUID orderId) {
        String userId = tokenAuthenticator.resolveTokenFromHeader(headers);
        return ResponseEntity.ok(orderService.approveOrder(userId, orderId));
    }

    @Operation(summary = "주문 취소", description = "구매자 확인 후 주문이 대기 상태라면 주문을 취소합니다.")
    @PutMapping("{orderId}/cancel")
    ResponseEntity<?> cancelOrder(@RequestHeader HttpHeaders headers,
                                  @NotNull(message = "orderId is must not be null") @PathVariable(name = "orderId") UUID orderId) {
        String userId = tokenAuthenticator.resolveTokenFromHeader(headers);
        return ResponseEntity.ok(orderService.cancelOrder(userId, orderId));
    }

    @Operation(summary = "주문 거부", description = "판매자 확인 후 주문이 대기 상태라면 주문을 거부합니다.")
    @PreAuthorize("hasRole('ROLE_SELLER')")
    @PutMapping("/{orderId}/reject")
    ResponseEntity<?> rejectOrder(@RequestHeader HttpHeaders headers,
                                  @NotNull(message = "orderId is must not be null") @PathVariable(name = "orderId") UUID orderId) {
        String userId = tokenAuthenticator.resolveTokenFromHeader(headers);
        return ResponseEntity.ok(orderService.rejectOrder(userId, orderId));
    }

    @Operation(summary = "본인 주문 조회", description = "아이디로 조회되는 주문 리스트를 반환합니다.")
    @GetMapping("/self")
    ResponseEntity<?> getOrdersByUser(@RequestHeader HttpHeaders headers,
                                      @PageableDefault Pageable pageable) {
        String userId = tokenAuthenticator.resolveTokenFromHeader(headers);
        return ResponseEntity.ok(orderService.getOrdersByUser(userId, pageable));
    }

    @Operation(summary = "상품 주문 정보 조회", description = "상품으로 들어온 모든 주문을 반환합니다.")
    @PreAuthorize("hasRole('ROLE_SELLER')")
    @GetMapping("/product/{productId}")
    ResponseEntity<?> getOrdersByProduct(@RequestHeader HttpHeaders headers,
                                         @NotNull(message = "productId must not be null") @PathVariable(name = "productId") UUID productId,
                                         @PageableDefault Pageable pageable) {
        String userId = tokenAuthenticator.resolveTokenFromHeader(headers);
        return ResponseEntity.ok(orderService.getOrdersByProduct(userId, productId, pageable));
    }

    @Operation(summary = "상품 구매", description = "장바구니에 있는 상품의 상태를 확인하며 구매합니다.")
    @PostMapping
    ResponseEntity<?> purchase(@RequestHeader HttpHeaders headers) {
        String userId = tokenAuthenticator.resolveTokenFromHeader(headers);
        return ResponseEntity.ok(orderService.purchase(userId));
    }

}
