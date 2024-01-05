package com.zerobase.commerce.api.user.dto;

import com.zerobase.commerce.database.constant.AuthorityStatus;
import com.zerobase.commerce.database.domain.User;
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
    private Set<AuthorityStatus> roles;
    private LocalDateTime registeredAt;
    private LocalDateTime updatedAt;

    public static UserDto fromEntity(User user) {
        return UserDto.builder()
                .id(user.getId())
                .password(user.getPassword())
                .roles(user.getRoles())
                .registeredAt(user.getRegisteredAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
