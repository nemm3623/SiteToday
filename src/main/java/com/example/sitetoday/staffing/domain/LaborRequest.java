package com.example.sitetoday.staffing.domain;

import com.example.sitetoday.common.exception.BusinessException;
import com.example.sitetoday.common.exception.CommonErrorCode;
import java.time.LocalDateTime;
import java.util.UUID;

public record LaborRequest(UUID id, UUID officeId, UUID employerId, LocalDateTime startsAt,
                           LocalDateTime endsAt, String workDescription, int workerCount, long dailyWage) {
    public LaborRequest {
        if (id == null || officeId == null || employerId == null || startsAt == null || endsAt == null
                || !endsAt.isAfter(startsAt) || workDescription == null || workDescription.isBlank()
                || workerCount < 1 || dailyWage < 1) {
            throw new BusinessException(CommonErrorCode.INVALID_REQUEST);
        }
    }
}
