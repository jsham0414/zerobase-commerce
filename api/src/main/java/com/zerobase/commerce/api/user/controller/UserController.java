package com.zerobase.commerce.api.user.controller;

import com.zerobase.commerce.api.security.TokenAuthenticator;
import com.zerobase.commerce.api.user.dto.UpdateUserInfo;
import com.zerobase.commerce.api.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "유저 컨트롤러", description = "유저 정보를 관리하는 엔드포인트들을 제공합니다.")
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final TokenAuthenticator tokenAuthenticator;

    @Operation(summary = "유저 정보 보기", description = "유저 정보를 반환합니다.")
    @GetMapping
    ResponseEntity<?> getUserInfo(@RequestHeader HttpHeaders headers,
                                  @NotBlank(message = "password must not be blank")
                                  @RequestBody String password) {
        String userId = tokenAuthenticator.resolveTokenFromHeader(headers);
        return ResponseEntity.ok(userService.getUserInfo(userId, password));
    }

    @Operation(summary = "유저 정보 수정", description = "비밀번호를 확인하고 유저의 정보를 수정합니다.")
    @PutMapping
    ResponseEntity<?> updateUserInfo(@RequestHeader HttpHeaders headers,
                                     @Validated @RequestBody UpdateUserInfo request) {
        String userId = tokenAuthenticator.resolveTokenFromHeader(headers);
        return ResponseEntity.ok(userService.updateUserInfo(userId, request));
    }

    @Operation(summary = "유저 삭제", description = "비밀번호를 확인하고 유저를 삭제합니다.")
    @DeleteMapping
    ResponseEntity<?> deleteUserInfo(@RequestHeader HttpHeaders headers,
                                     @NotBlank(message = "password must not be blank") @RequestBody String password) {
        String userId = tokenAuthenticator.resolveTokenFromHeader(headers);
        userService.deleteUserInfo(userId, password);
        return ResponseEntity.ok(null);
    }

    @Operation(summary = "판매자 등록", description = "유저에게 판매자 권한을 부여합니다.")
    @PutMapping("/seller")
    ResponseEntity<?> grantSeller(@RequestHeader HttpHeaders headers,
                                  @NotBlank(message = "password must not be blank") @RequestBody String password) {
        String userId = tokenAuthenticator.resolveTokenFromHeader(headers);
        return ResponseEntity.ok(userService.grantSeller(userId, password));
    }

}
