package com.zerobase.commerce.api.review.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.commerce.api.review.dto.ModifyReview;
import com.zerobase.commerce.api.review.dto.WriteReview;
import com.zerobase.commerce.api.review.service.ReviewService;
import com.zerobase.commerce.api.security.TokenAuthenticator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewController.class)
class ReviewControllerTest {
    @MockBean
    private ReviewService reviewService;

    @MockBean
    private TokenAuthenticator tokenAuthenticator;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getReview() throws Exception {
        var result = mockMvc.perform(get("/review/{reviewId}", 1));

        result.andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void getSelfReviews() throws Exception {
        var result = mockMvc.perform(get("/review/self"));

        result.andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void getProductReviews() throws Exception {
        var result = mockMvc.perform(get("/review/product/{productId}", UUID.randomUUID()));

        result.andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void writeReview() throws Exception {
        WriteReview writeReview = WriteReview.builder()
                .orderId(UUID.randomUUID())
                .productId(UUID.randomUUID())
                .userId("test")
                .star(4.2)
                .build();

        var result = mockMvc.perform(post("/review")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(writeReview)));

        result.andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void modifyReview() throws Exception {
        ModifyReview modifyReview = ModifyReview.builder()
                .id(1L)
                .star(4.2)
                .build();

        var result = mockMvc.perform(put("/review")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(modifyReview)));

        result.andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void deleteReview() throws Exception {
        var result = mockMvc.perform(delete("/review/{reviewId}", 1));

        result.andExpect(status().isOk())
                .andDo(print());
    }
}