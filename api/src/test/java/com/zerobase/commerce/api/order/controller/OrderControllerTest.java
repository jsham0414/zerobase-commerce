package com.zerobase.commerce.api.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.commerce.api.order.service.OrderService;
import com.zerobase.commerce.api.security.TokenAuthenticator;
import com.zerobase.commerce.database.order.constant.OrderStatus;
import com.zerobase.commerce.database.order.domain.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderControllerTest {
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    @MockBean
    private TokenAuthenticator tokenAuthenticator;

    @Autowired
    private MockMvc mockMvc;

    private Order order;

    @BeforeEach
    void setUp() {
        order = Order.builder()
                .id(UUID.randomUUID())
                .productId(UUID.randomUUID())
                .userId("test")
                .price(1000L)
                .amount(1L)
                .discount(0.0)
                .purchasedAt(LocalDateTime.now())
                .status(OrderStatus.PENDING)
                .build();
    }

    @Test
    void getOrder() throws Exception {
        var result = mockMvc.perform(get("/order/{orderId}", order.getId())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(order)));

        result.andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @WithMockUser(roles = "SELLER")
    void approveOrder() throws Exception {
        var result = mockMvc.perform(put("/order/{orderId}/approve", order.getId())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(order)));

        result.andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @WithMockUser(roles = "SELLER")
    void cancelOrder() throws Exception {
        var result = mockMvc.perform(put("/order/{orderId}/cancel", order.getId())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(order)));

        result.andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @WithMockUser(roles = "SELLER")
    void rejectOrder() throws Exception {
        var result = mockMvc.perform(put("/order/{orderId}/reject", order.getId())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(order)));

        result.andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void getOrdersByUser() throws Exception {
        Order anotherOrder = Order.builder()
                .id(UUID.randomUUID())
                .productId(UUID.randomUUID())
                .userId("test")
                .status(OrderStatus.PENDING)
                .discount(0.0)
                .price(1000L)
                .purchasedAt(LocalDateTime.now())
                .build();

        List<Order> orders = List.of(order, anotherOrder);

        var result = mockMvc.perform(get("/order/self")
                .contentType("application/json"));

        result.andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @WithMockUser(roles = "SELLER")
    void getOrdersByProduct() throws Exception {
        var result = mockMvc.perform(get("/order/product/{productId}", order.getProductId())
                .contentType("application/json"));

        result.andExpect(status().isOk())
                .andDo(print());
    }
}