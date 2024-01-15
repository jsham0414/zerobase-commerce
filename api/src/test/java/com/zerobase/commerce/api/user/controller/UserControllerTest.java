package com.zerobase.commerce.api.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.commerce.api.security.TokenAuthenticator;
import com.zerobase.commerce.api.security.TokenProvider;
import com.zerobase.commerce.api.user.dto.UpdateUserInfo;
import com.zerobase.commerce.api.user.dto.UserDto;
import com.zerobase.commerce.api.user.service.UserService;
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

    @MockBean
    private TokenAuthenticator tokenAuthenticator;

    @Test
    @DisplayName("유저 정보 가져오기 성공")
    void getUserInfoSuccess() throws Exception {
        Set<AuthorityStatus> roles = new HashSet<>();
        roles.add(ROLE_MEMBER);

        given(userService.getUserInfo(any(), anyString())).willReturn(
                UserDto.builder()
                        .id("root1234")
                        .password("qwer1234")
                        .roles(roles)
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
        Set<AuthorityStatus> roles = new HashSet<>();
        roles.add(ROLE_MEMBER);

        given(userService.updateUserInfo(any(), any())).willReturn(
                UserDto.builder()
                        .id("root1234")
                        .password("zxcv1234")
                        .roles(roles)
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