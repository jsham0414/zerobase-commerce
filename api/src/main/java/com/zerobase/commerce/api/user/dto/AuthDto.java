package com.zerobase.commerce.api.user.dto;

import com.zerobase.commerce.database.domain.User;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

public class AuthDto {
    public static class Request {
        @Data
        @Builder
        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class SignUp {
            private String id;
            private String password;

            public User toEntity(Set<String> roles, LocalDateTime dateTime) {
                return User.builder()
                        .id(id)
                        .password(password)
                        .roles(roles)
                        .registeredAt(dateTime)
                        .updatedAt(dateTime)
                        .build();
            }
        }

        @Data
        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class SignIn {
            private String id;
            private String password;
        }
    }


    public static class Response {
        @Data
        @Builder
        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class SignUp {
            private String id;
            private String password;
            private Set<String> roles;
        }

        @Data
        @Builder
        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class SignIn {
            private String id;
            private String password;
            private Set<String> roles;
        }
    }

}
