package com.zerobase.commerce.api.user.controller;

import com.zerobase.commerce.api.user.dto.UpdateUserInfo;
import com.zerobase.commerce.api.user.dto.UserDto;
import com.zerobase.commerce.api.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @GetMapping
    ResponseEntity<?> getUserInfo(@RequestHeader HttpHeaders headers, @RequestBody String password) {
        return ResponseEntity.ok(userService.getUserInfo(headers, password));
    }

    @PutMapping
    void updateUserInfo(@RequestHeader HttpHeaders headers, @RequestBody UpdateUserInfo request) {
        userService.updateUserInfo(headers, request);
    }

    @DeleteMapping
    void deleteUserInfo(@RequestHeader HttpHeaders headers, @RequestBody String password) {
        userService.deleteUserInfo(headers, password);
    }

}
