package com.zerobase.commerce.api.product.service;

import com.zerobase.commerce.api.exception.CustomException;
import com.zerobase.commerce.api.exception.ErrorCode;
import com.zerobase.commerce.api.product.dto.AddProduct;
import com.zerobase.commerce.api.product.dto.ProductDto;
import com.zerobase.commerce.api.security.TokenAuthenticator;
import com.zerobase.commerce.database.constant.ProductStatus;
import com.zerobase.commerce.database.domain.Product;
import com.zerobase.commerce.database.domain.User;
import com.zerobase.commerce.database.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final TokenAuthenticator tokenAuthenticator;
    private final UserRepository userRepository;

    public ProductDto addProduct(HttpHeaders headers, AddProduct.Request request) {
        String id = tokenAuthenticator.resolveTokenFromHeader(headers);
        User user = userRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.INVALID_USER_ID)
        );

        LocalDateTime now = LocalDateTime.now();

        ProductStatus status;
        if (request.getStatus() == null) {
            status = ProductStatus.PUBLIC;
        } else {
            status = ProductStatus.valueOf(request.getStatus());
        }

        Product product = Product.builder()
                .name(request.getName())
                .sellerId(user.getId())
                .price(request.getPrice())
                .status(status)
                .discount(0.0)
                .star(0.0)
                .publishedAt(now)
                .updatedAt(now)
                .build();

        return ProductDto.fromEntity(product);
    }
}
