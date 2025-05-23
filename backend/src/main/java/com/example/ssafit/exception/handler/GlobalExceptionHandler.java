package com.example.ssafit.exception.handler;

import com.example.ssafit.exception.CustomBusinessException;
import com.example.ssafit.exception.CustomUnAuthenticationException;
import com.example.ssafit.exception.ErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomUnAuthenticationException.class)
    public ResponseEntity CustomUnAuthenticationExceptionHandler(CustomUnAuthenticationException e) {
        ErrorCode errorCode = e.getErrorCode();

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(Map.of(
                        "code", errorCode.getCode(),
                        "message", errorCode.getMessage()
                ));
    }

    @ExceptionHandler(CustomBusinessException.class)
    public ResponseEntity CustomBusinessExceptionHandler(CustomBusinessException e) {
        ErrorCode errorCode = e.getErrorCode();

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(Map.of(
                        "code", errorCode.getCode(),
                        "message", errorCode.getMessage()
                ));
    }
}
