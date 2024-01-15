package com.zerobase.commerce.api.user.service;

import com.zerobase.commerce.api.constant.CacheKey;
import com.zerobase.commerce.api.exception.CustomException;
import com.zerobase.commerce.api.exception.ErrorCode;
import com.zerobase.commerce.api.security.TokenProvider;
import com.zerobase.commerce.api.user.dto.SignInDto;
import com.zerobase.commerce.api.user.dto.SignUpDto;
import com.zerobase.commerce.database.user.constant.AuthorityStatus;
import com.zerobase.commerce.database.user.domain.User;
import com.zerobase.commerce.database.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService implements UserDetailsService {
    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final RedisTemplate<String, Object> redisTemplate;

    @Transactional
    public SignUpDto.Response signUp(SignUpDto.Request request) {
        if (userRepository.existsById(request.getId())) {
            throw new CustomException(ErrorCode.USER_ID_DUPLICATED);
        }

        Set<AuthorityStatus> roles = new HashSet<>();
        roles.add(AuthorityStatus.ROLE_MEMBER);

        LocalDateTime now = LocalDateTime.now();
        User user = userRepository.save(request.toEntity(roles, now));

        return SignUpDto.Response.builder()
                .id(user.getId())
                .password(user.getPassword())
                .roles(user.getRoles())
                .build();
    }

    public String signIn(SignInDto.Request request) {
        User user = userRepository.findById(request.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.SIGN_IN_FAILED));

        if (!Objects.equals(user.getPassword(), request.getPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_INCORRECT);
        }

        return tokenProvider.generateToken(user.getId(), user.getRoles());
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        String key = CacheKey.KEY_USER + ":" + username;

        User user = (User) redisTemplate.opsForValue().get(key);

        if (user == null) {
            user = userRepository.findById(username).orElseThrow(
                    () -> new CustomException(ErrorCode.INVALID_USER_ID)
            );
            redisTemplate.opsForValue().set(key, user, 600, TimeUnit.SECONDS);
        }
        return user;
    }
}
