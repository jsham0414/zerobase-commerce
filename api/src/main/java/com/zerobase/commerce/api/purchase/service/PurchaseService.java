package com.zerobase.commerce.api.purchase.service;

import com.zerobase.commerce.api.exception.CustomException;
import com.zerobase.commerce.api.exception.ErrorCode;
import com.zerobase.commerce.api.order.dto.OrderDto;
import com.zerobase.commerce.api.security.TokenAuthenticator;
import com.zerobase.commerce.database.order.constant.OrderStatus;
import com.zerobase.commerce.database.order.domain.Order;
import com.zerobase.commerce.database.order.repository.OrderRepository;
import com.zerobase.commerce.database.product.constant.ProductStatus;
import com.zerobase.commerce.database.product.domain.Product;
import com.zerobase.commerce.database.product.repository.ProductRepository;
import com.zerobase.commerce.database.user.domain.User;
import com.zerobase.commerce.database.user.repository.UserRepository;
import com.zerobase.commerce.database.wishlist.domain.Wishlist;
import com.zerobase.commerce.database.wishlist.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PurchaseService {
    private final TokenAuthenticator tokenAuthenticator;
    private final UserRepository userRepository;
    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public List<OrderDto> purchase(HttpHeaders headers) {
        String userId = tokenAuthenticator.resolveTokenFromHeader(headers);
        if (!userRepository.existsById(userId)) {
            throw new CustomException(ErrorCode.INVALID_USER_ID);
        }

        User u = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.INVALID_USER_ID));

        List<Wishlist> wishlists = wishlistRepository.findByUserId(userId);
        List<OrderDto> orders = new ArrayList<>();

        for (Wishlist w : wishlists) {
            Product p = productRepository.findById(w.getProductId()).orElseThrow(
                    () -> new CustomException(ErrorCode.INVALID_PRODUCT_ID)
            );

            if (p.getStatus() == ProductStatus.PRIVATE)
                throw new CustomException(ErrorCode.PRIVATE_PRODUCT);

            Order order = orderRepository.save(Order.builder()
                    .userId(userId)
                    .productId(p.getId())
                    .price(p.getPrice())
                    .discount(p.getDiscount())
                    .amount(w.getAmount())
                    .status(OrderStatus.PENDING)
                    .purchasedAt(LocalDateTime.now())
                    .build()
            );

            orders.add(OrderDto.fromEntity(order));
        }

        wishlistRepository.deleteAllByUserId(userId);

        return orders;
    }
}
