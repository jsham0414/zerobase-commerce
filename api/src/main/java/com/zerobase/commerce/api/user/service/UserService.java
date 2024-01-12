package com.zerobase.commerce.api.user.service;

import com.zerobase.commerce.api.constant.CacheKey;
import com.zerobase.commerce.api.exception.CustomException;
import com.zerobase.commerce.api.exception.ErrorCode;
import com.zerobase.commerce.api.security.TokenAuthenticator;
import com.zerobase.commerce.api.user.dto.UpdateUserInfo;
import com.zerobase.commerce.api.user.dto.UserDto;
import com.zerobase.commerce.database.user.constant.AuthorityStatus;
import com.zerobase.commerce.database.user.domain.User;
import com.zerobase.commerce.database.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final TokenAuthenticator tokenAuthenticator;
    private final RedisTemplate<String, Object> redisTemplate;

    public UserDto getUserInfo(HttpHeaders headers, String password) {
        String id = tokenAuthenticator.resolveTokenFromHeader(headers);
        User user = userRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.INVALID_USER_ID)
        );

        if (!Objects.equals(user.getPassword(), password)) {
            throw new CustomException(ErrorCode.PASSWORD_INCORRECT);
        }

        return UserDto.fromEntity(user);
    }

    @Transactional
    public UserDto updateUserInfo(HttpHeaders headers, UpdateUserInfo request) {
        String id = tokenAuthenticator.resolveTokenFromHeader(headers);
        User user = userRepository.findByIdAndDecryptPassword(id).orElseThrow(
                () -> new CustomException(ErrorCode.INVALID_USER_ID)
        );

        if (!Objects.equals(user.getPassword(), request.getPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_INCORRECT);
        }

        if (Objects.nonNull(request.getNewPassword())) {
            user.setPassword(request.getNewPassword());
        }

        user.setUpdatedAt(LocalDateTime.now());

        redisTemplate.opsForValue().set(CacheKey.KEY_USER + ":" + user.getId(), user, 600, TimeUnit.SECONDS);

        return UserDto.fromEntity(user);
    }

    @Transactional
    public void deleteUserInfo(HttpHeaders headers, String password) {
        String id = tokenAuthenticator.resolveTokenFromHeader(headers);
        User user = userRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.INVALID_USER_ID)
        );

        if (!Objects.equals(user.getPassword(), password))
            throw new CustomException(ErrorCode.PASSWORD_INCORRECT);

        redisTemplate.opsForValue().getAndDelete(CacheKey.KEY_USER + ":" + user.getId());

        userRepository.delete(user);
    }

    @Transactional
    public UserDto grantSeller(HttpHeaders headers, String password) {
        String id = tokenAuthenticator.resolveTokenFromHeader(headers);
        User user = userRepository.findByIdAndDecryptPassword(id).orElseThrow(
                () -> new CustomException(ErrorCode.INVALID_USER_ID)
        );

        if (!Objects.equals(user.getPassword(), password)) {
            throw new CustomException(ErrorCode.PASSWORD_INCORRECT);
        }

        if (user.getRoles().contains(AuthorityStatus.ROLE_SELLER)) {
            throw new CustomException(ErrorCode.ALREADY_GRANTED);
        }

        user.getRoles().add(AuthorityStatus.ROLE_SELLER);

        return UserDto.fromEntity(user);
    }
}
