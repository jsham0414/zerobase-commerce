package com.zerobase.commerce.api.exception;

import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@ToString
public enum ErrorCode {
    UNEXPECTED_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred."),
    USER_ID_DUPLICATED(HttpStatus.BAD_REQUEST, "ID duplicated. Please enter a different ID."),
    INVALID_USER_ID(HttpStatus.FORBIDDEN, "The ID you entered is an ID that does not exist. Please enter a different ID."),
    SIGN_IN_FAILED(HttpStatus.BAD_REQUEST, "Login failed. Please check your input again."),
    PASSWORD_INCORRECT(HttpStatus.BAD_REQUEST, "Password is not equal. Please check your input again."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "Invalid token. Please check again."),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST),
    ALREADY_GRANTED(HttpStatus.BAD_REQUEST, "It's already granted. Please check again."),
    ALREADY_APPROVED(HttpStatus.BAD_REQUEST, "It's already approved. Please check again."),
    ALREADY_CANCELED(HttpStatus.BAD_REQUEST, "It's already canceled. Please check again."),
    ALREADY_REJECTED(HttpStatus.BAD_REQUEST, "It's already rejected. Please check again."),
    INVALID_PRODUCT_ID(HttpStatus.FORBIDDEN, "The ID you entered is an ID that does not exist. Please enter a different ID."),
    SELLER_ID_NOT_SAME(HttpStatus.FORBIDDEN, "Seller ID does not match, please enter a different ID."),
    USER_ID_NOT_SAME(HttpStatus.FORBIDDEN, "User ID does not match, please enter a different ID."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "Invalid request."),
    PRIVATE_PRODUCT(HttpStatus.BAD_REQUEST, "The status of this product is Private, it cannot be queried."),
    INVALID_WISHLIST_ID(HttpStatus.FORBIDDEN, "The ID you entered is an ID that does not exist. Please enter a different ID."),
    INVALID_ORDER_ID(HttpStatus.FORBIDDEN, "The ID you entered is an ID that does not exist. Please enter a different ID."),
    ALREADY_PROCESSED(HttpStatus.BAD_REQUEST, "It's already processed order. Please check again."),
    WRITTEN_REVIEW(HttpStatus.BAD_REQUEST, "It's already written review. Please check again."),
    INVALID_REVIEW_ID(HttpStatus.FORBIDDEN, "The ID you entered is an ID that does not exist. Please enter a different ID."),
    DELETED_PRODUCT(HttpStatus.BAD_REQUEST, "It's deleted product. Please check again.");

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
