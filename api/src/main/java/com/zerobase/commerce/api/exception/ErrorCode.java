package com.zerobase.commerce.api.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    UNEXPECTED_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred."),
    USER_ID_DUPLICATED(HttpStatus.BAD_REQUEST, "ID duplicated. Please enter a different ID."),
    INVALID_USER_ID(HttpStatus.BAD_REQUEST, "The ID you entered is an ID that does not exist. Please enter a different ID."),
    SIGN_IN_FAILED(HttpStatus.BAD_REQUEST, "Login failed. Please check your input again."),
    PASSWORD_INCORRECT(HttpStatus.BAD_REQUEST, "Password is not equal. Please check your input again."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "Invalid token. Please check again."),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST);

    private final HttpStatus statusCode;
    private final String errorCode;
    private final String errorMessage;

    ErrorCode(HttpStatus statusCode) {
        this.statusCode = statusCode;
        this.errorCode = "E" + String.format("%03d", this.ordinal());
        this.errorMessage = "";
    }

    ErrorCode(HttpStatus statusCode, String errorMessage) {
        this.statusCode = statusCode;
        this.errorCode = "E" + String.format("%03d", this.ordinal());
        this.errorMessage = errorMessage;
    }

    ErrorCode(HttpStatus statusCode, String errorMessage, String errorCode) {
        this.statusCode = statusCode;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
