package com.zerobase.commerce.api.user.dto;

import com.zerobase.commerce.database.constant.AuthorityStatus;
import com.zerobase.commerce.database.domain.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

public class SignUpDto {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        @NotBlank(message = "id must not be blank")
        private String id;
        @NotBlank(message = "password must not be blank")
        private String password;

        public User toEntity(Set<AuthorityStatus> roles, LocalDateTime dateTime) {
            return User.builder()
                    .id(id)
                    .password(password)
                    .roles(roles)
                    .registeredAt(dateTime)
                    .updatedAt(dateTime)
                    .build();
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private String id;
        private String password;
        private Set<AuthorityStatus> roles;
    }
}
