package com.example.sitetoday.account.domain;

import com.example.sitetoday.common.exception.ErrorCode;

public enum AccountErrorCode implements ErrorCode {
    EMAIL_ALREADY_REGISTERED(Category.CONFLICT, "이미 가입된 이메일입니다."),
    OFFICE_REGISTRATION_RESTRICTED(Category.FORBIDDEN, "사무소 담당자는 공개 가입할 수 없습니다.");

    private final Category category;
    private final String message;

    AccountErrorCode(Category category, String message) {
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
