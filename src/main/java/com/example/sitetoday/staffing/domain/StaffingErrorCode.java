package com.example.sitetoday.staffing.domain;

import com.example.sitetoday.common.exception.ErrorCode;

public enum StaffingErrorCode implements ErrorCode {
    OFFICE_NOT_FOUND;

    public Category category() {
        return Category.NOT_FOUND;
    }

    public String message() {
        return "사무소를 찾을 수 없습니다.";
    }
}
