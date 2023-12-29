package com.zerobase.commerce.api.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class CustomExceptionHandler {
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<?> customExceptionHandler(CustomException e) {
        return new ErrorResponse(e).toResponseEntity();
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> runtimeExceptionHandler(Exception e) {
        log.error("{} is occurred.", e.getCause().toString());

        return new ErrorResponse(
                new CustomException(ErrorCode.UNEXPECTED_ERROR)
        ).toResponseEntity();
    }
}
