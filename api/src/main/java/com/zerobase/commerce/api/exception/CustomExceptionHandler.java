package com.zerobase.commerce.api.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

@RestControllerAdvice
@Slf4j
public class CustomExceptionHandler {
    private final String SEPARATOR = ", ";

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<?> customExceptionHandler(CustomException e) {
        log.info("{} : {}", e.getErrorCode().getErrorCode(), e.getErrorCode().getErrorMessage());

        return new ErrorResponse(e).toResponseEntity();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> validationException(MethodArgumentNotValidException e) {
        var errorMessages = e.getBindingResult().getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList();
        return new ErrorResponse(new CustomException(ErrorCode.VALIDATION_ERROR)).toResponseEntity(String.join(SEPARATOR, errorMessages) + ".");
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<?> validationException(HandlerMethodValidationException e) {
        var validations = e.getAllValidationResults();

        StringBuilder sb = new StringBuilder();
        for (var validation : validations) {
            for (var errors : validation.getResolvableErrors()) {
                sb.append(errors.getDefaultMessage()).append(SEPARATOR);
            }
        }

        return new ErrorResponse(new CustomException(ErrorCode.VALIDATION_ERROR)).toResponseEntity(sb.delete(sb.length() - SEPARATOR.length(), sb.length()) + ".");
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> runtimeExceptionHandler(Exception e) {
        log.error("{} is occurred.", e.getMessage());

        return new ErrorResponse(
                new CustomException(ErrorCode.UNEXPECTED_ERROR)
        ).toResponseEntity();
    }
}
