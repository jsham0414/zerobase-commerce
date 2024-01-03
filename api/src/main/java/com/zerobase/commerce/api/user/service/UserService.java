package com.zerobase.commerce.api.user.service;

import com.zerobase.commerce.api.exception.CustomException;
import com.zerobase.commerce.api.exception.ErrorCode;
import com.zerobase.commerce.api.security.TokenAuthenticator;
import com.zerobase.commerce.api.user.dto.UpdateUserInfo;
import com.zerobase.commerce.api.user.dto.UserDto;
import com.zerobase.commerce.database.domain.User;
import com.zerobase.commerce.database.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final TokenAuthenticator tokenAuthenticator;

    public UserDto getUserInfo(HttpHeaders headers, String password) {
        String id = tokenAuthenticator.resolveTokenFromHeader(headers);
        User user = userRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.INVALID_USER_ID)
        );

        if (!Objects.equals(user.getPassword(), password))
            throw new CustomException(ErrorCode.PASSWORD_INCORRECT);

        return UserDto.builder()
                .id(user.getId())
                .password(user.getPassword())
                .roles(user.getRoles())
                .registeredAt(user.getRegisteredAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    @Transactional
    public UserDto updateUserInfo(HttpHeaders headers, UpdateUserInfo request) {
        String id = tokenAuthenticator.resolveTokenFromHeader(headers);
        User user = userRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.INVALID_USER_ID)
        );

        if (!Objects.equals(user.getPassword(), request.getPassword()))
            throw new CustomException(ErrorCode.PASSWORD_INCORRECT);

        if (Objects.nonNull(request.getNewPassword()))
            user.setPassword(request.getNewPassword());

        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);

        return UserDto.builder()
                .id(id)
                .password(user.getPassword())
                .registeredAt(user.getRegisteredAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    @Transactional
    public void deleteUserInfo(HttpHeaders headers, String password) {
        String id = tokenAuthenticator.resolveTokenFromHeader(headers);
        User user = userRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.INVALID_USER_ID)
        );

        if (!Objects.equals(user.getPassword(), password))
            throw new CustomException(ErrorCode.PASSWORD_INCORRECT);

        userRepository.delete(user);
    }
}
