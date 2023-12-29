package com.zerobase.commerce.api.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class UserDto {
    private String id;
    private String password;
    private Set<String> roles;
    private LocalDateTime registeredAt;
    private LocalDateTime updatedAt;
}
