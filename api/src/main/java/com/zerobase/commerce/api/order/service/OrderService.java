package com.zerobase.commerce.api.order.service;

import com.zerobase.commerce.api.exception.CustomException;
import com.zerobase.commerce.api.exception.ErrorCode;
import com.zerobase.commerce.api.order.dto.OrderDto;
import com.zerobase.commerce.api.security.TokenAuthenticator;
import com.zerobase.commerce.database.order.constant.OrderStatus;
import com.zerobase.commerce.database.order.domain.Order;
import com.zerobase.commerce.database.order.repository.OrderRepository;
import com.zerobase.commerce.database.product.domain.Product;
import com.zerobase.commerce.database.product.repository.ProductRepository;
import com.zerobase.commerce.database.user.domain.User;
import com.zerobase.commerce.database.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {
    private final OrderRepository orderRepository;
    private final TokenAuthenticator tokenAuthenticator;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public OrderDto getOrder(HttpHeaders headers, UUID id) {
        String userId = tokenAuthenticator.resolveTokenFromHeader(headers);
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.INVALID_USER_ID)
        );

        Order order = orderRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.INVALID_ORDER_ID)
        );

        if (!Objects.equals(user.getId(), order.getUserId())) {
            throw new CustomException(ErrorCode.USER_ID_NOT_SAME);
        }

        return OrderDto.fromEntity(order);
    }

    @Transactional
    public OrderDto approveOrder(HttpHeaders headers, UUID id) {
        String userId = tokenAuthenticator.resolveTokenFromHeader(headers);
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.INVALID_USER_ID)
        );

        Order order = orderRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.INVALID_ORDER_ID)
        );

        if (!Objects.equals(user.getId(), order.getUserId())) {
            throw new CustomException(ErrorCode.USER_ID_NOT_SAME);
        }

        Product product = productRepository.findById(order.getProductId()).orElseThrow(
                () -> new CustomException(ErrorCode.INVALID_PRODUCT_ID)
        );

        if (!Objects.equals(user.getId(), product.getSellerId())) {
            throw new CustomException(ErrorCode.SELLER_ID_NOT_SAME);
        }

        if (Objects.equals(order.getStatus(), OrderStatus.APPROVED)) {
            throw new CustomException(ErrorCode.ALREADY_APPROVED);
        }

        if (!Objects.equals(order.getStatus(), OrderStatus.PENDING)) {
            throw new CustomException(ErrorCode.ALREADY_PROCESSED);
        }

        order.setStatus(OrderStatus.APPROVED);

        return OrderDto.fromEntity(orderRepository.save(order));
    }

    @Transactional
    public OrderDto cancelOrder(HttpHeaders headers, UUID id) {
        String userId = tokenAuthenticator.resolveTokenFromHeader(headers);
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.INVALID_USER_ID)
        );

        Order order = orderRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.INVALID_ORDER_ID)
        );

        if (!Objects.equals(user.getId(), order.getUserId())) {
            throw new CustomException(ErrorCode.USER_ID_NOT_SAME);
        }

        if (Objects.equals(order.getStatus(), OrderStatus.CANCELED)) {
            throw new CustomException(ErrorCode.ALREADY_CANCELED);
        }

        if (!Objects.equals(order.getStatus(), OrderStatus.PENDING)) {
            throw new CustomException(ErrorCode.ALREADY_PROCESSED);
        }

        order.setStatus(OrderStatus.CANCELED);

        return OrderDto.fromEntity(orderRepository.save(order));
    }

    @Transactional
    public OrderDto rejectOrder(HttpHeaders headers, UUID id) {
        String userId = tokenAuthenticator.resolveTokenFromHeader(headers);
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.INVALID_USER_ID)
        );

        Order order = orderRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.INVALID_ORDER_ID)
        );

        if (!Objects.equals(user.getId(), order.getUserId())) {
            throw new CustomException(ErrorCode.USER_ID_NOT_SAME);
        }

        Product product = productRepository.findById(order.getProductId()).orElseThrow(
                () -> new CustomException(ErrorCode.INVALID_PRODUCT_ID)
        );

        if (!Objects.equals(user.getId(), product.getSellerId())) {
            throw new CustomException(ErrorCode.SELLER_ID_NOT_SAME);
        }

        if (Objects.equals(order.getStatus(), OrderStatus.REJECTED)) {
            throw new CustomException(ErrorCode.ALREADY_REJECTED);
        }

        if (!Objects.equals(order.getStatus(), OrderStatus.PENDING)) {
            throw new CustomException(ErrorCode.ALREADY_PROCESSED);
        }

        order.setStatus(OrderStatus.REJECTED);

        return OrderDto.fromEntity(orderRepository.save(order));
    }

    public List<OrderDto> getOrdersByUser(HttpHeaders headers) {
        String userId = tokenAuthenticator.resolveTokenFromHeader(headers);

        return orderRepository.findByUserIdOrderByPurchasedAtDesc(userId)
                .stream()
                .map(OrderDto::fromEntity)
                .toList();
    }

    public List<OrderDto> getOrdersByProduct(HttpHeaders headers, UUID id) {
        String userId = tokenAuthenticator.resolveTokenFromHeader(headers);

        Product product = productRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.INVALID_PRODUCT_ID)
        );

        if (!Objects.equals(userId, product.getSellerId())) {
            throw new CustomException(ErrorCode.SELLER_ID_NOT_SAME);
        }

        return orderRepository.findByProductIdOrderByPurchasedAtDesc(id)
                .stream()
                .map(OrderDto::fromEntity)
                .toList();
    }
}
