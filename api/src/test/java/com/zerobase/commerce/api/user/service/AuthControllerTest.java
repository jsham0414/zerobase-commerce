package com.zerobase.commerce.api.user.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.commerce.api.security.TokenProvider;
import com.zerobase.commerce.api.user.controller.AuthController;
import com.zerobase.commerce.api.user.dto.AuthDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.Set;

import static com.zerobase.commerce.database.constant.AuthorityStatus.ROLE_MEMBER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@ActiveProfiles("test")
class AuthControllerTest {
    @MockBean
    private AuthService authService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TokenProvider tokenProvider;

    @Test
    @DisplayName("회원가입 성공")
    void signUpSuccess() throws Exception {
        Set<String> roles = new HashSet<>();
        roles.add(ROLE_MEMBER.name());

        var request = AuthDto.Request.SignUp.builder()
                .id("root1234")
                .password("qwer1234")
                .build();

        given(authService.signUp(any())).willReturn(
                AuthDto.Response.SignUp.builder()
                        .id("root1234")
                        .password("qwer1234")
                        .roles(roles)
                        .build());

        var result = mockMvc.perform(post("/auth/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("root1234"))
                .andExpect(jsonPath("$.password").value("qwer1234"))
                .andExpect(jsonPath("$.roles[0]").value(ROLE_MEMBER.name()))
                .andDo(print());
    }

    @Test
    @DisplayName("로그인 성공")
    void signInSuccess() throws Exception {
        var request = AuthDto.Request.SignUp.builder()
                .id("root1234")
                .password("qwer1234")
                .build();

        var result = mockMvc.perform(post("/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        result.andExpect(status().isOk());
    }
}