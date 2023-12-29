package com.zerobase.commerce.api.user.service;

import com.zerobase.commerce.api.exception.CustomException;
import com.zerobase.commerce.api.exception.ErrorCode;
import com.zerobase.commerce.api.user.dto.AuthDto;
import com.zerobase.commerce.database.constant.AuthorityStatus;
import com.zerobase.commerce.database.domain.User;
import com.zerobase.commerce.database.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {
    private final UserRepository userRepository;

    @Transactional
    public AuthDto.Response.SignUp signUp(AuthDto.Request.SignUp request) {
        if (userRepository.existsById(request.getId())) {
            throw new CustomException(ErrorCode.USER_ID_DUPLICATED);
        }

        Set<String> roles = new HashSet<>();
        roles.add(AuthorityStatus.ROLE_MEMBER.toString());

        LocalDateTime now = LocalDateTime.now();
        User user = userRepository.save(request.toEntity(roles, now));

        return AuthDto.Response.SignUp.builder()
                .id(user.getId())
                .password(user.getPassword())
                .roles(user.getRoles())
                .build();
    }

    public AuthDto.Response.SignIn signIn(AuthDto.Request.SignIn request) {
        User user = userRepository.findById(request.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.SIGN_IN_FAILED));

        if (!Objects.equals(user.getPassword(), request.getPassword())) {
            throw new CustomException(ErrorCode.SIGN_IN_FAILED);
        }

        return AuthDto.Response.SignIn.builder()
                .id(user.getId())
                .password(user.getPassword())
                .roles(user.getRoles())
                .build();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findById(username).orElseThrow(() -> new UsernameNotFoundException("couldn't find id " + username));
    }
}
