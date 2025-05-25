package com.example.ssafit.exception;

import org.springframework.http.HttpStatus;

import java.io.IOException;

public class CustomInbodyException extends IOException {

    private final ErrorCode errorCode;

    public CustomInbodyException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public CustomInbodyException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() { return errorCode; }

    public HttpStatus getHttpStatus() { return errorCode.getHttpStatus(); }
}
