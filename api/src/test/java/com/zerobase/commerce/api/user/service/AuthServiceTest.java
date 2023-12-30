package com.zerobase.commerce.api.user.service;

import com.zerobase.commerce.api.exception.CustomException;
import com.zerobase.commerce.api.exception.ErrorCode;
import com.zerobase.commerce.api.user.dto.AuthDto;
import com.zerobase.commerce.database.domain.User;
import com.zerobase.commerce.database.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Transactional
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    UserRepository userRepository;

    @InjectMocks
    AuthService authService;

    @Test
    @DisplayName("회원가입 성공")
    void signUpSuccess() {
        // given
        User user = User.builder()
                .id("asdf1234")
                .password("qwer1234")
                .roles(Arrays.stream(new String[]{"ROLE_MEMBER"}).collect(Collectors.toSet()))
                .registeredAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        var request = AuthDto.Request.SignUp.builder()
                .id("asdf1234")
                .password("qwer1234")
                .build();

        given(userRepository.existsById(anyString())).willReturn(false);
        given(userRepository.save(any())).willReturn(user);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        // when
        AuthDto.Response.SignUp response = authService.signUp(request);

        // then
        verify(userRepository, times(1)).save(captor.capture());

        assertEquals(response.getId(), user.getId());
        assertEquals(response.getPassword(), user.getPassword());

        assertEquals(captor.getValue().getId(), user.getId());
        assertEquals(captor.getValue().getPassword(), user.getPassword());
    }

    @Test
    @DisplayName("회원가입 실패 - 중복 아이디 입력")
    void signUpFailedDuplicateId() {
        // given
        User user = User.builder()
                .id("asdf1234")
                .password("qwer1234")
                .roles(Arrays.stream(new String[]{"ROLE_MEMBER"}).collect(Collectors.toSet()))
                .registeredAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();


        given(userRepository.existsById(anyString())).willReturn(true);

        // when
        CustomException exception = assertThrows(
                CustomException.class,
                () -> authService.signUp(
                        AuthDto.Request.SignUp.builder()
                                .id(user.getId())
                                .password(user.getPassword())
                                .build()
                )
        );

        // then
        verify(userRepository, times(0)).save(any());
        assertEquals(exception.getErrorCode(), ErrorCode.USER_ID_DUPLICATED);
    }

    @Test
    @DisplayName("로그인 성공")
    void signInSuccess() {
        // given
        User user = User.builder()
                .id("asdf1234")
                .password("qwer1234")
                .roles(Arrays.stream(new String[]{"ROLE_MEMBER"}).collect(Collectors.toSet()))
                .registeredAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        given(userRepository.findById(anyString())).willReturn(Optional.of(user));

        // when
        AuthDto.Response.SignIn response = authService.signIn(AuthDto.Request.SignIn.builder()
                .id("asdf1234")
                .password("qwer1234")
                .build()
        );

        // then
        assertEquals(response.getId(), user.getId());
        assertEquals(response.getPassword(), user.getPassword());
    }

    @Test
    @DisplayName("로그인 실패 - 아이디를 찾을 수 없음")
    void signInFailedIdNotFound() {
        // given
        User user = User.builder()
                .id("asdf1234")
                .password("qwer1234")
                .roles(Arrays.stream(new String[]{"ROLE_MEMBER"}).collect(Collectors.toSet()))
                .registeredAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        given(userRepository.findById(anyString())).willReturn(Optional.empty());

        // when
        CustomException exception = assertThrows(
                CustomException.class,
                () -> authService.signIn(
                        AuthDto.Request.SignIn.builder()
                                .id(user.getId())
                                .password(user.getPassword())
                                .build())
        );

        // then
        assertEquals(exception.getErrorCode(), ErrorCode.SIGN_IN_FAILED);
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호가 틀림")
    void signInFailedPasswordIncorrect() {
        // given
        User user = User.builder()
                .id("asdf1234")
                .password("qwer1234")
                .roles(Arrays.stream(new String[]{"ROLE_MEMBER"}).collect(Collectors.toSet()))
                .registeredAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        given(userRepository.findById(anyString())).willReturn(Optional.of(user));

        // when
        CustomException exception = assertThrows(
                CustomException.class,
                () -> authService.signIn(
                        AuthDto.Request.SignIn.builder()
                                .id(user.getId())
                                .password("qwer1233")
                                .build())
        );

        // then
        assertEquals(exception.getErrorCode(), ErrorCode.PASSWORD_INCORRECT);
    }
}