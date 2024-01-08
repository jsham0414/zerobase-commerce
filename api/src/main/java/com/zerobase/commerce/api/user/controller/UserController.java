package com.zerobase.commerce.api.user.controller;

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

    @GetMapping
    ResponseEntity<?> getUserInfo(@RequestHeader HttpHeaders headers,
                                  @NotBlank(message = "password must not be blank")
                                  @RequestBody String password) {
        return ResponseEntity.ok(userService.getUserInfo(headers, password));
    }

    @PutMapping
    ResponseEntity<?> updateUserInfo(@RequestHeader HttpHeaders headers,
                                     @Validated @RequestBody UpdateUserInfo request) {
        return ResponseEntity.ok(userService.updateUserInfo(headers, request));
    }

    @DeleteMapping
    ResponseEntity<?> deleteUserInfo(@RequestHeader HttpHeaders headers,
                                     @NotBlank(message = "password must not be blank") @RequestBody String password) {
        userService.deleteUserInfo(headers, password);
        return ResponseEntity.ok(null);
    }

    @PutMapping("/seller")
    ResponseEntity<?> grantSeller(@RequestHeader HttpHeaders headers,
                                  @NotBlank(message = "password must not be blank") @RequestBody String password) {
        return ResponseEntity.ok(userService.grantSeller(headers, password));
    }

}
