package com.zerobase.commerce.api.user.controller;

import com.zerobase.commerce.api.security.TokenProvider;
import com.zerobase.commerce.api.user.service.AuthService;
import com.zerobase.commerce.api.user.dto.AuthDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final TokenProvider tokenProvider;

    @PostMapping("/sign-up")
    ResponseEntity<?> signUp(@RequestBody AuthDto.Request.SignUp request) {
        return ResponseEntity.ok(authService.signUp(request));
    }

    @PostMapping("/sign-in")
    ResponseEntity<?> signIn(@RequestBody AuthDto.Request.SignIn request) {
        AuthDto.Response.SignIn userDto = authService.signIn(request);
        String token = tokenProvider.generateToken(userDto.getId(), userDto.getRoles());

        return ResponseEntity.ok(token);
    }
}
