package com.example.sitetoday.staffing.presentation;

import com.example.sitetoday.staffing.application.EmployerStaffingService;
import com.example.sitetoday.staffing.application.RegisterLaborRequestCommand;
import com.example.sitetoday.staffing.presentation.dto.OfficeSummaryResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/offices")
class EmployerStaffingController {
    private final EmployerStaffingService service;

    EmployerStaffingController(EmployerStaffingService service) {
        this.service = service;
    }

    record RegisterRequest(@NotNull LocalDateTime startsAt, @NotNull LocalDateTime endsAt,
                           @NotBlank String workDescription, @NotNull @Positive Integer workerCount,
                           @NotNull @Positive Long dailyWage) {}
    record RegisterResponse(UUID requestId) {}

    @GetMapping
    List<OfficeSummaryResponse> offices() {
        return service.listOffices().stream()
                .map(result -> new OfficeSummaryResponse(result.officeId(), result.name()))
                .toList();
    }

    @PostMapping("/{officeId}/labor-requests")
    @ResponseStatus(HttpStatus.CREATED)
    RegisterResponse register(@PathVariable UUID officeId, @Valid @RequestBody RegisterRequest body, Principal user) {
        var command = new RegisterLaborRequestCommand(UUID.fromString(user.getName()), officeId,
                body.startsAt(), body.endsAt(), body.workDescription(), body.workerCount(), body.dailyWage());
        return new RegisterResponse(service.register(command).requestId());
    }
}
