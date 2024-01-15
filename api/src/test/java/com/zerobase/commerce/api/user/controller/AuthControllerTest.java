package com.zerobase.commerce.api.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.commerce.api.security.TokenAuthenticator;
import com.zerobase.commerce.api.user.dto.SignInDto;
import com.zerobase.commerce.api.user.dto.SignUpDto;
import com.zerobase.commerce.api.user.service.AuthService;
import com.zerobase.commerce.database.user.constant.AuthorityStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.Set;

import static com.zerobase.commerce.database.user.constant.AuthorityStatus.ROLE_MEMBER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {
    @MockBean
    private AuthService authService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TokenAuthenticator tokenAuthenticator;

    @Test
    @DisplayName("회원가입 성공")
    void signUpSuccess() throws Exception {
        var request = SignUpDto.Request.builder()
                .id("root1234")
                .password("qwer1234")
                .build();

        Set<AuthorityStatus> roles = new HashSet<>();
        roles.add(ROLE_MEMBER);

        given(authService.signUp(any())).willReturn(
                SignUpDto.Response.builder()
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
        var request = SignInDto.Request.builder()
                .id("root1234")
                .password("qwer1234")
                .build();

        given(authService.signIn(any())).willReturn("token");

        var result = mockMvc.perform(post("/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        result.andExpect(status().isOk());
    }

}