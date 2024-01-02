package com.zerobase.commerce.api.user.controller;

import com.zerobase.commerce.api.exception.ErrorCode;
import com.zerobase.commerce.api.user.dto.SignInDto;
import com.zerobase.commerce.api.user.dto.SignUpDto;
import com.zerobase.commerce.api.user.dto.UpdateUserInfo;
import com.zerobase.commerce.api.user.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @PostMapping("/sign-up")
    ResponseEntity<?> signUp(@Validated @RequestBody SignUpDto.Request request) {
        return ResponseEntity.ok(userService.signUp(request));
    }

    @PostMapping("/sign-in")
    ResponseEntity<?> signIn(@Validated @RequestBody SignInDto.Request request) {
        return ResponseEntity.ok(userService.signIn(request));
    }

    @GetMapping
    ResponseEntity<?> getUserInfo(@RequestHeader HttpHeaders headers, @NotBlank(message = "password must not be blank") @RequestBody String password) {
        return ResponseEntity.ok(userService.getUserInfo(headers, password));
    }

    @PutMapping
    ResponseEntity<?> updateUserInfo(@RequestHeader HttpHeaders headers, @Validated @RequestBody UpdateUserInfo request) {
        return ResponseEntity.ok(userService.updateUserInfo(headers, request));
    }

    @DeleteMapping
    ResponseEntity<?> deleteUserInfo(@RequestHeader HttpHeaders headers, @NotBlank(message = "password must not be blank") @RequestBody String password) {
        userService.deleteUserInfo(headers, password);
        return ResponseEntity.ok(null);
    }

}
