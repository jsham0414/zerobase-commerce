package com.zerobase.commerce.api.user.service;

import com.zerobase.commerce.api.user.dto.AuthDto;
import com.zerobase.commerce.database.domain.User;
import com.zerobase.commerce.database.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@Transactional
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    UserRepository userRepository;

    @Autowired
    AuthService authService;

    @Test
    void signUpSuccess() {
        Optional<User> user = Optional.of(
                User.builder()
                        .id("asdf1234")
                        .password("qwer1234")
                        .roles(Arrays.stream(new String[] {"ROLE_MEMBER"}).collect(Collectors.toSet()))
                        .build()
        );

        given(authService.signUp(any())).willReturn(AuthDto.Response.SignUp.builder().build());

        assertEquals();
    }

    @Test
    void signInSuccess() {

    }

    @Test
    void loadUserByUsernameSuccess() {

    }
}