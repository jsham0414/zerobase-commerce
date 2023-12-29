package com.zerobase.commerce.api.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UpdateUserInfo {
    private String password;
    private String newPassword;
}
