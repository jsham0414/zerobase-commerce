package com.zerobase.commerce.api.user.service;

import com.zerobase.commerce.api.exception.CustomException;
import com.zerobase.commerce.api.exception.ErrorCode;
import com.zerobase.commerce.api.security.TokenProvider;
import com.zerobase.commerce.api.user.dto.SignInDto;
import com.zerobase.commerce.api.user.dto.SignUpDto;
import com.zerobase.commerce.api.user.dto.UpdateUserInfo;
import com.zerobase.commerce.api.user.dto.UserDto;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Transactional
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    UserRepository userRepository;

    @Mock
    TokenProvider tokenProvider;

    @InjectMocks
    UserService userService;


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

        var request = SignUpDto.Request.builder()
                .id("asdf1234")
                .password("qwer1234")
                .build();

        given(userRepository.existsById(anyString())).willReturn(false);
        given(userRepository.save(any())).willReturn(user);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        // when
        SignUpDto.Response response = userService.signUp(request);

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
                () -> userService.signUp(
                        SignUpDto.Request.builder()
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
        String response = userService.signIn(SignInDto.Request.builder()
                .id("asdf1234")
                .password("qwer1234")
                .build()
        );

        // then
        assertNotNull(response);
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
                () -> userService.signIn(
                        SignInDto.Request.builder()
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
                () -> userService.signIn(
                        SignInDto.Request.builder()
                                .id(user.getId())
                                .password("qwer1233")
                                .build())
        );

        // then
        assertEquals(exception.getErrorCode(), ErrorCode.PASSWORD_INCORRECT);
    }

    @Test
    @DisplayName("유저 정보 가져오기 성공")
    void getUserInfoSuccess() {
        // given
        User user = User.builder()
                .id("asdf1234")
                .password("qwer1234")
                .roles(Arrays.stream(new String[]{"ROLE_MEMBER"}).collect(Collectors.toSet()))
                .registeredAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        given(tokenProvider.resolveTokenFromHeader(any())).willReturn("asdf1234");
        given(userRepository.findById(anyString())).willReturn(Optional.of(user));

        // when
        UserDto userDto = userService.getUserInfo(any(), "qwer1234");

        // then
        assertEquals(userDto.getId(), user.getId());
        assertEquals(userDto.getPassword(), user.getPassword());
    }

    @Test
    @DisplayName("유저 정보 가져오기 실패 - 아이디를 찾을 수 없음")
    void getUserInfoFailedIdNotFound() {
        // given
        User user = User.builder()
                .id("asdf1234")
                .password("qwer1234")
                .roles(Arrays.stream(new String[]{"ROLE_MEMBER"}).collect(Collectors.toSet()))
                .registeredAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        given(tokenProvider.resolveTokenFromHeader(any())).willReturn("asdf1233");
        given(userRepository.findById(anyString())).willReturn(Optional.empty());

        // when
        CustomException exception = assertThrows(
                CustomException.class,
                () -> userService.getUserInfo(any(), "qwer1234")
        );

        // then
        assertEquals(exception.getErrorCode(), ErrorCode.INVALID_USER_ID);
    }

    @Test
    @DisplayName("유저 정보 가져오기 실패 - 비밀번호가 틀림")
    void getUserInfoPasswordIncorrect() {
        // given
        User user = User.builder()
                .id("asdf1234")
                .password("qwer1234")
                .roles(Arrays.stream(new String[]{"ROLE_MEMBER"}).collect(Collectors.toSet()))
                .registeredAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        given(tokenProvider.resolveTokenFromHeader(any())).willReturn("asdf1234");
        given(userRepository.findById(anyString())).willReturn(Optional.of(user));

        // when
        CustomException exception = assertThrows(
                CustomException.class,
                () -> userService.getUserInfo(any(), "qwer1233")
        );

        // then
        assertEquals(exception.getErrorCode(), ErrorCode.PASSWORD_INCORRECT);
    }

    @Test
    @DisplayName("유저 정보 수정 성공")
    void updateUserInfoSuccess() {
        // given
        User user = User.builder()
                .id("asdf1234")
                .password("qwer1234")
                .roles(Arrays.stream(new String[]{"ROLE_MEMBER"}).collect(Collectors.toSet()))
                .registeredAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        String newPassword = "zxcv1122";

        given(tokenProvider.resolveTokenFromHeader(any())).willReturn("asdf1234");
        given(userRepository.findById(anyString())).willReturn(Optional.of(user));

        // when
        UserDto userDto = userService.updateUserInfo(any(), UpdateUserInfo.builder()
                .password("qwer1234")
                .newPassword(newPassword)
                .build());

        // then
        assertEquals(userDto.getPassword(), newPassword);
    }

    @Test
    @DisplayName("유저 삭제 성공")
    void deleteUserInfoSuccess() {
        // given
        User user = User.builder()
                .id("asdf1234")
                .password("qwer1234")
                .roles(Arrays.stream(new String[]{"ROLE_MEMBER"}).collect(Collectors.toSet()))
                .registeredAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        given(tokenProvider.resolveTokenFromHeader(any())).willReturn("asdf1234");
        given(userRepository.findById(anyString())).willReturn(Optional.of(user));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        // when
        userService.deleteUserInfo(any(), "qwer1234");

        // then
        verify(userRepository, times(1)).delete(captor.capture());
    }
}