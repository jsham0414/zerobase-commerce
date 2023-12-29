package com.zerobase.commerce.api.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@ToString
@Getter
public class CustomException extends RuntimeException {
    private ErrorCode errorCode;
}
