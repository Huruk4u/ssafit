package com.example.ssafit.exception;

import com.google.api.Http;
import org.springframework.http.HttpStatus;

public enum ErrorCode {
    
    // USER TOKEN 인증 관련 오류 처리
    USER_NOT_FOUND("AUTH_001", "유저를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INVALID_PASSWORD("AUTH_002", "잘못된 비밀번호를 입력했습니다.", HttpStatus.UNAUTHORIZED),
    USER_SUSPENDED("AUTH_004", "계정이 정지되었습니다.", HttpStatus.FORBIDDEN),

    // Report 관련 오류 처리
    SELF_REPORTED("REPORT_001", "자기 자신을 신고할 수는 없습니다.", HttpStatus.BAD_REQUEST),
    DUPLICATED_REPORT("REPORT_002", "중복된 신고입니다.", HttpStatus.CONFLICT),
    ADMIN_REPORTED("REPORT_003", "운영자는 신고 대상이 아닙니다.", HttpStatus.FORBIDDEN),
    ALREADY_HANDLED("REPORT_004", "이미 처리된 신고입니다.", HttpStatus.UNPROCESSABLE_ENTITY)
    ;

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
