package com.example.sitetoday.staffing.application;

import java.time.LocalDateTime;
import java.util.UUID;

public record RegisterLaborRequestCommand(
        UUID employerId, UUID officeId, LocalDateTime startsAt, LocalDateTime endsAt,
        String workDescription, int workerCount, long dailyWage) {}
