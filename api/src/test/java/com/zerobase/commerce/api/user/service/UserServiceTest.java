package com.zerobase.commerce.api.user.service;

import com.zerobase.commerce.api.exception.CustomException;
import com.zerobase.commerce.api.exception.ErrorCode;
import com.zerobase.commerce.api.security.TokenAuthenticator;
import com.zerobase.commerce.api.user.dto.UpdateUserInfo;
import com.zerobase.commerce.api.user.dto.UserDto;
import com.zerobase.commerce.database.user.constant.AuthorityStatus;
import com.zerobase.commerce.database.user.domain.User;
import com.zerobase.commerce.database.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.zerobase.commerce.database.user.constant.AuthorityStatus.ROLE_MEMBER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    TokenAuthenticator tokenAuthenticator;

    @InjectMocks
    UserService userService;

    @Test
    @DisplayName("유저 정보 가져오기 성공")
    void getUserInfoSuccess() {
        // given
        Set<AuthorityStatus> roles = new HashSet<>();
        roles.add(ROLE_MEMBER);

        User user = User.builder()
                .id("asdf1234")
                .password("qwer1234")
                .roles(roles)
                .registeredAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        given(tokenAuthenticator.resolveTokenFromHeader(any())).willReturn("asdf1234");
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
        Set<AuthorityStatus> roles = new HashSet<>();
        roles.add(ROLE_MEMBER);

        User user = User.builder()
                .id("asdf1234")
                .password("qwer1234")
                .roles(roles)
                .registeredAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        given(tokenAuthenticator.resolveTokenFromHeader(any())).willReturn("asdf1233");
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
        Set<AuthorityStatus> roles = new HashSet<>();
        roles.add(ROLE_MEMBER);

        User user = User.builder()
                .id("asdf1234")
                .password("qwer1234")
                .roles(roles)
                .registeredAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        given(tokenAuthenticator.resolveTokenFromHeader(any())).willReturn("asdf1234");
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
        Set<AuthorityStatus> roles = new HashSet<>();
        roles.add(ROLE_MEMBER);

        User user = User.builder()
                .id("asdf1234")
                .password("qwer1234")
                .roles(roles)
                .registeredAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        String newPassword = "zxcv1122";

        given(tokenAuthenticator.resolveTokenFromHeader(any())).willReturn("asdf1234");
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
        Set<AuthorityStatus> roles = new HashSet<>();
        roles.add(ROLE_MEMBER);

        User user = User.builder()
                .id("asdf1234")
                .password("qwer1234")
                .roles(roles)
                .registeredAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        given(tokenAuthenticator.resolveTokenFromHeader(any())).willReturn("asdf1234");
        given(userRepository.findById(anyString())).willReturn(Optional.of(user));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        // when
        userService.deleteUserInfo(any(), "qwer1234");

        // then
        verify(userRepository, times(1)).delete(captor.capture());
    }
}