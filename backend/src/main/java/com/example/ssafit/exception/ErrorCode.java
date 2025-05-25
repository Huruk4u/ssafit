package com.example.ssafit.exception;

import com.google.api.Http;
import org.springframework.http.HttpStatus;

public enum ErrorCode {
    
    // USER TOKEN 인증 관련 오류 처리
    USER_NOT_FOUND("AUTH_001", "유저를 찾을 수 없습니다.", HttpStatus.UNAUTHORIZED),
    INVALID_PASSWORD("AUTH_002", "잘못된 비밀번호를 입력했습니다.", HttpStatus.UNAUTHORIZED),
    USER_SUSPENDED("AUTH_004", "계정이 정지되었습니다.", HttpStatus.FORBIDDEN),

    // Report 관련 오류 처리
    SELF_REPORTED("REPORT_001", "자기 자신을 신고할 수는 없습니다.", HttpStatus.BAD_REQUEST),
    DUPLICATED_REPORT("REPORT_002", "중복된 신고입니다.", HttpStatus.CONFLICT),
    ADMIN_REPORTED("REPORT_003", "운영자는 신고 대상이 아닙니다.", HttpStatus.FORBIDDEN),
    ALREADY_HANDLED("REPORT_004", "이미 처리된 신고입니다.", HttpStatus.UNPROCESSABLE_ENTITY),

    // Article 관련 오류 처리
    ARTICLE_NOT_FOUND("BOARD_001", "게시글이 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    BAD_APPROACH("BOARD_002", "다른 사용자의 게시글을 수정할 수 없습니다.", HttpStatus.FORBIDDEN),
    ARTICLE_MODIFY_FAILED("BOARD_003", "게시글 수정 중 문제가 발생했습니다.", HttpStatus.BAD_REQUEST),
    ARTICLE_REMOVE_FAILED("BOARD_004", "게시글 삭제 중 문제가 발생했습니다.", HttpStatus.BAD_REQUEST),

    // Comment 관련 오류 처리
    COMMENT_CREATE_FAILED("BOARD_005", "댓글 생성 중 문제가 발생했습니다.", HttpStatus.BAD_REQUEST),
    COMMENT_NOT_FOUND("BOARD_006", "댓글이 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    COMMENT_AUTHOR_NOT_FOUND("BOARD_007", "작성자 정보를 찾을 수 없습니다.", HttpStatus.NO_CONTENT),
    COMMENT_MODIFY_FAILED("BOARD_008", "댓글 수정 중 문제가 발생했습니다.", HttpStatus.BAD_REQUEST),
    COMMENT_REMOVE_FAILED("BOARD_009", "댓글 삭제 중 문제가 발생했습니다.", HttpStatus.BAD_REQUEST),

    // OCR 추출 실패 오류 처리
    OCR_INVALID_IMAGE_FORMAT("CHALLENGE_001", "OCR파싱에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    GPT_GET_RESPONSE_FAILED("CHALLENGE_002", "GPT API의 응답을 얻는 중 문제가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

    // Report 관련 오류 처리
    REPORTEE_NOT_FOUND("REPORT_001", "유저를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    REPORT_CREATE_FAILED("REPORT_002", "신고 중 오류가 발생했습니다.", HttpStatus.BAD_REQUEST),

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
