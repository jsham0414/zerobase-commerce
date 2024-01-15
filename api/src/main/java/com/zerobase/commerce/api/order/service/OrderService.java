package com.zerobase.commerce.api.order.service;

import com.zerobase.commerce.api.exception.CustomException;
import com.zerobase.commerce.api.exception.ErrorCode;
import com.zerobase.commerce.api.order.dto.OrderDto;
import com.zerobase.commerce.database.order.constant.OrderStatus;
import com.zerobase.commerce.database.order.domain.Order;
import com.zerobase.commerce.database.order.repository.OrderRepository;
import com.zerobase.commerce.database.product.constant.ProductStatus;
import com.zerobase.commerce.database.product.domain.Product;
import com.zerobase.commerce.database.product.repository.ProductRepository;
import com.zerobase.commerce.database.user.repository.UserRepository;
import com.zerobase.commerce.database.wishlist.domain.Wishlist;
import com.zerobase.commerce.database.wishlist.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final WishlistRepository wishlistRepository;

    public OrderDto getOrder(String userId, UUID id) {
        Order order = orderRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.INVALID_ORDER_ID)
        );

        if (!Objects.equals(userId, order.getUserId())) {
            throw new CustomException(ErrorCode.USER_ID_NOT_SAME);
        }

        return OrderDto.fromEntity(order);
    }

    @Transactional
    public OrderDto approveOrder(String userId, UUID id) {
        Order order = orderRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.INVALID_ORDER_ID)
        );

        if (!Objects.equals(userId, order.getUserId())) {
            throw new CustomException(ErrorCode.USER_ID_NOT_SAME);
        }

        Product product = productRepository.findById(order.getProductId()).orElseThrow(
                () -> new CustomException(ErrorCode.INVALID_PRODUCT_ID)
        );

        if (!Objects.equals(userId, product.getSellerId())) {
            throw new CustomException(ErrorCode.SELLER_ID_NOT_SAME);
        }

        if (Objects.equals(order.getStatus(), OrderStatus.APPROVED)) {
            throw new CustomException(ErrorCode.ALREADY_APPROVED);
        }

        if (!Objects.equals(order.getStatus(), OrderStatus.PENDING)) {
            throw new CustomException(ErrorCode.ALREADY_PROCESSED);
        }

        order.setStatus(OrderStatus.APPROVED);

        return OrderDto.fromEntity(order);
    }

    @Transactional
    public OrderDto cancelOrder(String userId, UUID id) {
        Order order = orderRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.INVALID_ORDER_ID)
        );

        if (!Objects.equals(userId, order.getUserId())) {
            throw new CustomException(ErrorCode.USER_ID_NOT_SAME);
        }

        if (Objects.equals(order.getStatus(), OrderStatus.CANCELED)) {
            throw new CustomException(ErrorCode.ALREADY_CANCELED);
        }

        if (!Objects.equals(order.getStatus(), OrderStatus.PENDING)) {
            throw new CustomException(ErrorCode.ALREADY_PROCESSED);
        }

        order.setStatus(OrderStatus.CANCELED);

        return OrderDto.fromEntity(order);
    }

    @Transactional
    public OrderDto rejectOrder(String userId, UUID id) {
        Order order = orderRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.INVALID_ORDER_ID)
        );

        if (!Objects.equals(userId, order.getUserId())) {
            throw new CustomException(ErrorCode.USER_ID_NOT_SAME);
        }

        Product product = productRepository.findById(order.getProductId()).orElseThrow(
                () -> new CustomException(ErrorCode.INVALID_PRODUCT_ID)
        );

        if (!Objects.equals(userId, product.getSellerId())) {
            throw new CustomException(ErrorCode.SELLER_ID_NOT_SAME);
        }

        if (Objects.equals(order.getStatus(), OrderStatus.REJECTED)) {
            throw new CustomException(ErrorCode.ALREADY_REJECTED);
        }

        if (!Objects.equals(order.getStatus(), OrderStatus.PENDING)) {
            throw new CustomException(ErrorCode.ALREADY_PROCESSED);
        }

        order.setStatus(OrderStatus.REJECTED);

        return OrderDto.fromEntity(order);
    }

    public List<OrderDto> getOrdersByUser(String userId, Pageable pageable) {
        return orderRepository.findByUserIdOrderByPurchasedAtDesc(userId, pageable)
                .stream()
                .map(OrderDto::fromEntity)
                .toList();
    }

    public List<OrderDto> getOrdersByProduct(String sellerId, UUID id, Pageable pageable) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.INVALID_PRODUCT_ID)
        );

        if (!Objects.equals(sellerId, product.getSellerId())) {
            throw new CustomException(ErrorCode.SELLER_ID_NOT_SAME);
        }

        return orderRepository.findByProductIdOrderByPurchasedAtDesc(id, pageable)
                .stream()
                .map(OrderDto::fromEntity)
                .toList();
    }

    @Transactional
    public List<OrderDto> purchase(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new CustomException(ErrorCode.INVALID_USER_ID);
        }

        List<Wishlist> wishlists = wishlistRepository.findByUserId(userId, Pageable.unpaged());
        List<OrderDto> orders = new ArrayList<>();

        for (Wishlist w : wishlists) {
            Product p = productRepository.findById(w.getProductId()).orElseThrow(
                    () -> new CustomException(ErrorCode.INVALID_PRODUCT_ID)
            );

            if (p.getStatus() == ProductStatus.PRIVATE)
                throw new CustomException(ErrorCode.PRIVATE_PRODUCT);

            if (p.getStatus() == ProductStatus.DELETED)
                throw new CustomException(ErrorCode.DELETED_PRODUCT);

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
