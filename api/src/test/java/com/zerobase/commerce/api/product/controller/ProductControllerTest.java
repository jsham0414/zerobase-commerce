package com.zerobase.commerce.api.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.commerce.api.product.dto.AddProduct;
import com.zerobase.commerce.api.product.dto.UpdateProduct;
import com.zerobase.commerce.api.product.service.ProductService;
import com.zerobase.commerce.api.security.TokenAuthenticator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private TokenAuthenticator tokenAuthenticator;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void validationTest() throws Exception {
        AddProduct addProduct = new AddProduct();
        var result = mockMvc.perform(post("/product")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(addProduct)));

        result.andExpect(status().isBadRequest())
                .andDo(print());

        UpdateProduct updateProduct = new UpdateProduct();
        result = mockMvc.perform(put("/product")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(updateProduct)));

        result.andExpect(status().isBadRequest())
                .andDo(print());

        result = mockMvc.perform(get("/product")
                .contentType("application/json"));

        result.andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "test", roles = "SELLER")
    void addProductSuccess() throws Exception {
        AddProduct addProduct = AddProduct.builder()
                .price(1000L)
                .name("코카콜라 뚱캔")
                .build();

        var result = mockMvc.perform(post("/product")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(addProduct)));

        result.andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void getProductSuccess() throws Exception {
        var result = mockMvc.perform(get("/product/{productId}", UUID.randomUUID())
                .contentType("application/json"));

        result.andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "test", roles = "SELLER")
    void updateProductSuccess() throws Exception {
        UpdateProduct updateProduct = UpdateProduct.builder()
                .id(UUID.randomUUID())
                .sellerId("test")
                .build();

        var result = mockMvc.perform(put("/product")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(updateProduct)));

        result.andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "test", roles = "SELLER")
    void deleteProductSuccess() throws Exception {
        var result = mockMvc.perform(delete("/product/{productId}", UUID.randomUUID())
                .contentType("application/json"));

        result.andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "test", roles = "SELLER")
    void getProductsBySellerSuccess() throws Exception {
        var result = mockMvc.perform(get("/product/seller")
                .contentType("application/json"));

        result.andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void getProductsSuccess() throws Exception {
        var result = mockMvc.perform(get("/product")
                .contentType("application/json")
                .param("name", "test")
                .param("filter", "PRICE")
                .param("order", "DESC"));

        result.andExpect(status().isOk())
                .andDo(print());
    }
}