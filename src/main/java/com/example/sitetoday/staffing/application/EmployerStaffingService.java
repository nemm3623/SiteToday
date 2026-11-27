package com.example.sitetoday.staffing.application;

import com.example.sitetoday.common.exception.BusinessException;
import com.example.sitetoday.staffing.domain.*;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmployerStaffingService {
    private final OfficeRepository offices;
    private final LaborRequestRepository requests;

    public EmployerStaffingService(OfficeRepository offices, LaborRequestRepository requests) {
        this.offices = offices;
        this.requests = requests;
    }

    @Transactional(readOnly = true)
    public List<OfficeSummaryResult> listOffices() {
        return offices.findAll().stream().map(office -> new OfficeSummaryResult(office.id(), office.name())).toList();
    }

    @Transactional
    public RegisterLaborRequestResult register(RegisterLaborRequestCommand command) {
        var request = new LaborRequest(UUID.randomUUID(), command.officeId(), command.employerId(), command.startsAt(),
                command.endsAt(), command.workDescription(), command.workerCount(), command.dailyWage());
        if (!offices.exists(request.officeId())) {
            throw new BusinessException(StaffingErrorCode.OFFICE_NOT_FOUND);
        }
        requests.save(request);
        return new RegisterLaborRequestResult(request.id());
    }
}
