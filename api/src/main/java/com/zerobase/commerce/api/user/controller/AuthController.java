package com.zerobase.commerce.api.user.controller;

import com.zerobase.commerce.api.user.dto.SignInDto;
import com.zerobase.commerce.api.user.dto.SignUpDto;
import com.zerobase.commerce.api.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/sign-up")
    ResponseEntity<?> signUp(@Validated @RequestBody SignUpDto.Request request) {
        return ResponseEntity.ok(authService.signUp(request));
    }

    @PostMapping("/sign-in")
    ResponseEntity<?> signIn(@Validated @RequestBody SignInDto.Request request) {
        return ResponseEntity.ok(authService.signIn(request));
    }
}
