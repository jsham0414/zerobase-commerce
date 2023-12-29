package com.zerobase.commerce.api.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

@AllArgsConstructor
public class ErrorResponse {
    private CustomException customException;

    public ResponseEntity<?> toResponseEntity() {
        var errorCode = customException.getErrorCode();

        return ResponseEntity.status(errorCode.getStatusCode()).body(
                ErrorResponseDto.builder()
                        .errorCode(errorCode.getErrorCode())
                        .errorMessage(errorCode.getErrorMessage())
                        .build()
        );
    }

    @Getter
    @AllArgsConstructor
    static class ErrorResponseDto {
        private String errorCode;
        private String errorMessage;
        private final LocalDateTime timeStamp;

        public static ErrorResponseDtoBuilder builder() {
            return new ErrorResponseDtoBuilder();
        }

        public static class ErrorResponseDtoBuilder {
            private String errorCode;
            private String errorMessage;
            private LocalDateTime timeStamp;

            ErrorResponseDtoBuilder() {
            }

            public ErrorResponseDtoBuilder errorCode(String errorCode) {
                this.errorCode = errorCode;
                return this;
            }

            public ErrorResponseDtoBuilder errorMessage(String errorMessage) {
                this.errorMessage = errorMessage;
                return this;
            }

            public ErrorResponseDto build() {
                return new ErrorResponseDto(this.errorCode, this.errorMessage, LocalDateTime.now());
            }

            public String toString() {
                return "ErrorResponse.ErrorResponseDto.ErrorResponseDtoBuilder(errorCode=" + this.errorCode + ", errorMessage=" + this.errorMessage + ", timeStamp=" + this.timeStamp + ")";
            }
        }
    }
}
