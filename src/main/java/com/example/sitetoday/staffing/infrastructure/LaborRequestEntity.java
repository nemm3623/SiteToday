package com.example.sitetoday.staffing.infrastructure;

import com.example.sitetoday.staffing.domain.LaborRequest;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "labor_requests")
class LaborRequestEntity {
    @Id private UUID id;
    private UUID officeId;
    private UUID employerId;
    private LocalDateTime startsAt;
    private LocalDateTime endsAt;
    private String workDescription;
    private int workerCount;
    private long dailyWage;

    protected LaborRequestEntity() {}

    LaborRequestEntity(LaborRequest request) {
        id = request.id();
        officeId = request.officeId();
        employerId = request.employerId();
        startsAt = request.startsAt();
        endsAt = request.endsAt();
        workDescription = request.workDescription();
        workerCount = request.workerCount();
        dailyWage = request.dailyWage();
    }
}
