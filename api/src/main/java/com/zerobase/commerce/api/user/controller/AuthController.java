package com.zerobase.commerce.api.user.controller;

import com.zerobase.commerce.api.user.dto.SignInDto;
import com.zerobase.commerce.api.user.dto.SignUpDto;
import com.zerobase.commerce.api.user.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "인증 컨트롤러", description = "회원가입, 로그인을 위한 엔드포인트를 제공합니다.")
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "회원가입", description = "아이디 중복 확인 후 생성된 계정 정보를 반환합니다.")
    @PostMapping("/sign-up")
    ResponseEntity<?> signUp(@Validated @RequestBody SignUpDto.Request request) {
        return ResponseEntity.ok(authService.signUp(request));
    }

    @Operation(summary = "로그인", description = "아이디와 비밀번호를 확인하고 발급된 토큰을 반환합니다.")
    @PostMapping("/sign-in")
    ResponseEntity<?> signIn(@Validated @RequestBody SignInDto.Request request) {
        return ResponseEntity.ok(authService.signIn(request));
    }
}
