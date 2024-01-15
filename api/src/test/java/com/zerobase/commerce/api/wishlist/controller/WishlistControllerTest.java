package com.zerobase.commerce.api.wishlist.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.commerce.api.security.TokenAuthenticator;
import com.zerobase.commerce.api.wishlist.dto.AddWishlist;
import com.zerobase.commerce.api.wishlist.dto.UpdateWishlist;
import com.zerobase.commerce.api.wishlist.service.WishlistService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WishlistController.class)
class WishlistControllerTest {
    @MockBean
    private WishlistService wishlistService;

    @MockBean
    private TokenAuthenticator tokenAuthenticator;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void addWishlist() throws Exception {
        var result = mockMvc.perform(get("/wishlist"));

        result.andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void getWishlist() throws Exception {
        AddWishlist addWishlist = AddWishlist.builder()
                .productId(UUID.randomUUID())
                .amount(1L)
                .build();

        var result = mockMvc.perform(post("/wishlist/{reviewId}", 1)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(addWishlist)));

        result.andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void updateWishlist() throws Exception {
        UpdateWishlist updateWishlist = UpdateWishlist.builder()
                .amount(1L)
                .build();

        var result = mockMvc.perform(put("/wishlist", 1)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(updateWishlist)));

        result.andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void deleteWishlist() throws Exception {
        var result = mockMvc.perform(delete("/wishlist/{wishlistId}", 1));

        result.andExpect(status().isOk())
                .andDo(print());
    }
}