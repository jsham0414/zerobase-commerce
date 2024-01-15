package com.zerobase.commerce.api.order.service;

import com.zerobase.commerce.api.order.dto.OrderDto;
import com.zerobase.commerce.database.order.constant.OrderStatus;
import com.zerobase.commerce.database.order.domain.Order;
import com.zerobase.commerce.database.order.repository.OrderRepository;
import com.zerobase.commerce.database.product.constant.ProductStatus;
import com.zerobase.commerce.database.product.domain.Product;
import com.zerobase.commerce.database.product.repository.ProductRepository;
import com.zerobase.commerce.database.user.repository.UserRepository;
import com.zerobase.commerce.database.wishlist.repository.WishlistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Transactional
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private WishlistRepository wishlistRepository;
    @InjectMocks
    private OrderService orderService;

    private Order order;
    private Product product;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();

        product = Product.builder()
                .id(UUID.randomUUID())
                .name("코카콜라 뚱캔")
                .sellerId("test")
                .price(1000L)
                .discount(0.0)
                .status(ProductStatus.PUBLIC)
                .updatedAt(now)
                .publishedAt(now)
                .star(4.5)
                .build();

        order = Order.builder()
                .id(UUID.randomUUID())
                .userId("test")
                .productId(product.getId())
                .price(1000L)
                .amount(1L)
                .status(OrderStatus.PENDING)
                .purchasedAt(now)
                .build();
    }

    @Test
    void getOrder() {
        given(orderRepository.findById(any()))
                .willReturn(Optional.of(order));

        OrderDto orderDto = orderService.getOrder(order.getUserId(), order.getId());

        verify(orderRepository, times(1)).findById(order.getId());

        assertAll(
                () -> assertEquals(orderDto.getId(), order.getId()),
                () -> assertEquals(orderDto.getUserId(), order.getUserId())
        );
    }

    @Test
    void approveOrder() {
        given(orderRepository.findById(any()))
                .willReturn(Optional.of(order));
        given(productRepository.findById(any()))
                .willReturn(Optional.of(product));

        OrderDto orderDto = orderService.approveOrder(order.getUserId(), order.getId());

        assertAll(
                () -> assertEquals(orderDto.getId(), order.getId()),
                () -> assertEquals(orderDto.getUserId(), order.getUserId()),
                () -> assertEquals(orderDto.getStatus(), OrderStatus.APPROVED)
        );
    }

    @Test
    void cancelOrder() {
        given(orderRepository.findById(any()))
                .willReturn(Optional.of(order));
        given(productRepository.findById(any()))
                .willReturn(Optional.of(product));

        OrderDto orderDto = orderService.cancelOrder(order.getUserId(), order.getId());

        assertAll(
                () -> assertEquals(orderDto.getId(), order.getId()),
                () -> assertEquals(orderDto.getUserId(), order.getUserId()),
                () -> assertEquals(orderDto.getStatus(), OrderStatus.CANCELED)
        );
    }

    @Test
    void rejectOrder() {
        given(orderRepository.findById(any()))
                .willReturn(Optional.of(order));
        given(productRepository.findById(any()))
                .willReturn(Optional.of(product));

        OrderDto orderDto = orderService.rejectOrder(order.getUserId(), order.getId());

        assertAll(
                () -> assertEquals(orderDto.getId(), order.getId()),
                () -> assertEquals(orderDto.getUserId(), order.getUserId()),
                () -> assertEquals(orderDto.getStatus(), OrderStatus.REJECTED)
        );
    }

    @Test
    void getOrdersByProduct() {
        Order anotherOrder = Order.builder()
                .id(UUID.randomUUID())
                .productId(product.getId())
                .userId("anotherUser")
                .purchasedAt(LocalDateTime.now())
                .discount(10.0)
                .price(1000L)
                .status(OrderStatus.APPROVED)
                .build();

        List<Order> orders = List.of(order, anotherOrder);

        given(productRepository.findById(any()))
                .willReturn(Optional.of(product));

        given(orderRepository.findByProductIdOrderByPurchasedAtDesc(any(), any()))
                .willReturn(orders);

        List<OrderDto> orderDtos = orderService.getOrdersByProduct("test", product.getId(), Pageable.unpaged());

        verify(productRepository, times(1)).findById(product.getId());
        verify(orderRepository, times(1)).findByProductIdOrderByPurchasedAtDesc(product.getId(), Pageable.unpaged());

        assertAll(
                () -> assertEquals(orderDtos.size(), 2)
        );
    }
}