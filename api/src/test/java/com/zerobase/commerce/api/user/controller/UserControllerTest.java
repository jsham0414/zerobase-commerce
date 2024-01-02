package com.zerobase.commerce.api.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.commerce.api.security.TokenProvider;
import com.zerobase.commerce.api.user.dto.*;
import com.zerobase.commerce.api.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.stream.Collectors;

import static com.zerobase.commerce.database.constant.AuthorityStatus.ROLE_MEMBER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {
    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TokenProvider tokenProvider;

    @Test
    @DisplayName("회원가입 성공")
    void signUpSuccess() throws Exception {
        var request = SignUpDto.Request.builder()
                .id("root1234")
                .password("qwer1234")
                .build();

        given(userService.signUp(any())).willReturn(
                SignUpDto.Response.builder()
                        .id("root1234")
                        .password("qwer1234")
                        .roles(Arrays.stream(new String[]{"ROLE_MEMBER"}).collect(Collectors.toSet()))
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

        given(userService.signIn(any())).willReturn("token");

        var result = mockMvc.perform(post("/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        result.andExpect(status().isOk());
    }

    @Test
    @DisplayName("유저 정보 가져오기 성공")
    void getUserInfoSuccess() throws Exception {
        given(userService.getUserInfo(any(), anyString())).willReturn(
                UserDto.builder()
                        .id("root1234")
                        .password("qwer1234")
                        .roles(Arrays.stream(new String[]{"ROLE_MEMBER"}).collect(Collectors.toSet()))
                        .build()
        );

        var result = mockMvc.perform(get("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString("qwer1234")));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("root1234"))
                .andExpect(jsonPath("$.password").value("qwer1234"))
                .andExpect(jsonPath("$.roles[0]").value(ROLE_MEMBER.name()))
                .andDo(print());
    }

    @Test
    @DisplayName("유저 정보 수정 성공")
    void updateUserInfoSuccess() throws Exception {
        given(userService.updateUserInfo(any(), any())).willReturn(
                UserDto.builder()
                        .id("root1234")
                        .password("zxcv1234")
                        .roles(Arrays.stream(new String[]{"ROLE_MEMBER"}).collect(Collectors.toSet()))
                        .build()
        );

        var result = mockMvc.perform(put("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        UpdateUserInfo.builder()
                                .password("qwer1234")
                                .newPassword("zxcv1234")
                                .build()
                )));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("root1234"))
                .andExpect(jsonPath("$.password").value("zxcv1234"))
                .andExpect(jsonPath("$.roles[0]").value(ROLE_MEMBER.name()))
                .andDo(print());
    }


    @Test
    @DisplayName("유저 정보 삭제 성공")
    void deleteUserInfoSuccess() throws Exception {
        var result = mockMvc.perform(delete("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString("qwer1234")));

        result.andExpect(status().isOk())
                .andDo(print());
    }
}