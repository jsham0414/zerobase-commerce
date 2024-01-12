package com.zerobase.commerce.api.user.controller;

import com.zerobase.commerce.api.security.TokenAuthenticator;
import com.zerobase.commerce.api.user.dto.UpdateUserInfo;
import com.zerobase.commerce.api.user.service.UserService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final TokenAuthenticator tokenAuthenticator;

    @GetMapping
    ResponseEntity<?> getUserInfo(@RequestHeader HttpHeaders headers,
                                  @NotBlank(message = "password must not be blank")
                                  @RequestBody String password) {
        String userId = tokenAuthenticator.resolveTokenFromHeader(headers);
        return ResponseEntity.ok(userService.getUserInfo(userId, password));
    }

    @PutMapping
    ResponseEntity<?> updateUserInfo(@RequestHeader HttpHeaders headers,
                                     @Validated @RequestBody UpdateUserInfo request) {
        String userId = tokenAuthenticator.resolveTokenFromHeader(headers);
        return ResponseEntity.ok(userService.updateUserInfo(userId, request));
    }

    @DeleteMapping
    ResponseEntity<?> deleteUserInfo(@RequestHeader HttpHeaders headers,
                                     @NotBlank(message = "password must not be blank") @RequestBody String password) {
        String userId = tokenAuthenticator.resolveTokenFromHeader(headers);
        userService.deleteUserInfo(userId, password);
        return ResponseEntity.ok(null);
    }

    @PutMapping("/seller")
    ResponseEntity<?> grantSeller(@RequestHeader HttpHeaders headers,
                                  @NotBlank(message = "password must not be blank") @RequestBody String password) {
        String userId = tokenAuthenticator.resolveTokenFromHeader(headers);
        return ResponseEntity.ok(userService.grantSeller(userId, password));
    }

}
