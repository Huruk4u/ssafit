package com.example.ssafit.exception;


import org.springframework.http.HttpStatus;

/**
 * 유저의 인증 실패를 처리하는 Exception
 */
public class CustomUnAuthenticationException extends RuntimeException {

    public final ErrorCode errorCode;
    
    public CustomUnAuthenticationException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public CustomUnAuthenticationException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public HttpStatus getHttpStatus() {
        return errorCode.getHttpStatus();
    }
}
