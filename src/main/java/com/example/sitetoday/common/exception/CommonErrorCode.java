package com.example.sitetoday.common.exception;

public enum CommonErrorCode implements ErrorCode {
    INVALID_REQUEST(Category.INVALID_INPUT, "요청 형식이나 입력값을 확인해 주세요."),
    AUTHENTICATION_REQUIRED(Category.UNAUTHENTICATED, "로그인이 필요합니다."),
    INVALID_CREDENTIALS(Category.UNAUTHENTICATED, "이메일 또는 비밀번호를 확인해 주세요."),
    ACCESS_DENIED(Category.FORBIDDEN, "접근이 거부되었습니다.");

    private final Category category;
    private final String message;

    CommonErrorCode(Category category, String message) {
        this.category = category;
        this.message = message;
    }

    @Override
    public Category category() {
        return category;
    }

    @Override
    public String message() {
        return message;
    }
}
