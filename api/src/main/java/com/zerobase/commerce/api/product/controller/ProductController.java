package com.zerobase.commerce.api.product.controller;

import com.zerobase.commerce.api.product.dto.AddProduct;
import com.zerobase.commerce.api.product.dto.SearchProductByName;
import com.zerobase.commerce.api.product.dto.UpdateProduct;
import com.zerobase.commerce.api.product.service.ProductService;
import com.zerobase.commerce.api.security.TokenAuthenticator;
import com.zerobase.commerce.api.validation.EnumCheck;
import com.zerobase.commerce.database.product.constant.ProductSortFilter;
import com.zerobase.commerce.database.product.constant.SortOrder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
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
@Tag(name = "상품 컨트롤러", description = "상품 관리와 조회를 위한 엔드포인트를 제공합니다.")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final TokenAuthenticator tokenAuthenticator;

    @Operation(summary = "상품 추가", description = "판매자일 경우 상품을 추가하고 그 결과를 반환합니다.")
    @PreAuthorize("hasRole('ROLE_SELLER')")
    @PostMapping
    ResponseEntity<?> addProduct(@RequestHeader HttpHeaders headers,
                                 @Validated @RequestBody AddProduct request) {
        String userId = tokenAuthenticator.resolveTokenFromHeader(headers);
        return ResponseEntity.ok(productService.addProduct(userId, request));
    }

    @Operation(summary = "상품 상세 보기", description = "상품이 유효한지 확인하고 반환합니다.")
    @GetMapping("/{productId}")
    ResponseEntity<?> getProduct(
            @NotNull(message = "productId must not be blank") @PathVariable(name = "productId") UUID productId) {
        return ResponseEntity.ok(productService.getProduct(productId));
    }

    @Operation(summary = "상품 정보 수정", description = "상품의 정보를 수정하고 반환합니다.")
    @PreAuthorize("hasRole('ROLE_SELLER')")
    @PutMapping
    ResponseEntity<?> updateProduct(@RequestHeader HttpHeaders headers,
                                    @Validated @RequestBody UpdateProduct request) {
        String userId = tokenAuthenticator.resolveTokenFromHeader(headers);
        return ResponseEntity.ok(productService.updateProduct(userId, request));
    }

    @Operation(summary = "상품 정보 수정", description = "판매자 확인 후 상품을 삭제됨 상태로 변경합니다.")
    @PreAuthorize("hasRole('ROLE_SELLER')")
    @DeleteMapping("/{productId}")
    ResponseEntity<?> deleteProduct(@RequestHeader HttpHeaders headers,
                                    @NotNull(message = "Id is must not be null") @PathVariable(name = "productId") UUID productId) {
        String userId = tokenAuthenticator.resolveTokenFromHeader(headers);
        productService.deleteProduct(userId, productId);
        return ResponseEntity.ok(null);
    }

    @Operation(summary = "판매자 상품 조회", description = "판매자가 개시한 상품의 리스트를 반환합니다.")
    @GetMapping("/seller")
    ResponseEntity<?> getProductsBySeller(@RequestHeader HttpHeaders headers,
                                          @PageableDefault Pageable pageable) {
        String userId = tokenAuthenticator.resolveTokenFromHeader(headers);
        return ResponseEntity.ok(productService.getProductsBySeller(userId, pageable));
    }

    @Operation(summary = "이름으로 상품 검색", description = "필터가 적용된 이름으로 검색된 결과를 반환합니다.")
    @GetMapping
    ResponseEntity<?> getProducts(@NotBlank(message = "Name is must not be blank") @RequestParam(name = "name") String name,
                                  @EnumCheck(check = ProductSortFilter.class, message = "filter is invalid") @RequestParam(name = "filter", required = false) String filter,
                                  @EnumCheck(check = SortOrder.class, message = "order is invalid") @RequestParam(name = "order", required = false) String order,
                                  @PageableDefault Pageable pageable) {
        SearchProductByName searchProductByName = SearchProductByName.builder()
                .name(name)
                .filter(filter)
                .order(order)
                .build();
        return ResponseEntity.ok(productService.getProductsByName(searchProductByName, pageable));
    }

    @Operation(summary = "상품 랭킹 조회", description = "별점이 높은 순으로 10개의 상품 리스트를 반환합니다.")
    @GetMapping("/ranking")
    ResponseEntity<?> getStarRanking() {
        return ResponseEntity.ok(productService.getStarRanking());
    }

}
