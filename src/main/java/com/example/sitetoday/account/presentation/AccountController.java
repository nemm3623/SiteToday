package com.example.sitetoday.account.presentation;

import com.example.sitetoday.account.application.MemberRegistrationService;
import com.example.sitetoday.account.presentation.dto.CurrentMemberResponse;
import com.example.sitetoday.account.presentation.dto.SignupRequest;
import com.example.sitetoday.account.presentation.dto.SignupResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
class AccountController {
    private final MemberRegistrationService service;

    AccountController(MemberRegistrationService service) {
        this.service = service;
    }

    @PostMapping("/members")
    ResponseEntity<SignupResponse> register(@RequestBody @Valid SignupRequest request) {
        var command = new MemberRegistrationService.Command(request.email(), request.password(), request.role());
        try {
            var response = new SignupResponse(service.register(command));
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (MemberRegistrationService.OfficeRegistrationRestricted exception) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (MemberRegistrationService.EmailAlreadyRegistered exception) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @GetMapping("/auth/csrf")
    String csrf(CsrfToken token) {
        return token.getToken();
    }

    @GetMapping("/auth/me")
    CurrentMemberResponse currentMember(Authentication auth) {
        String role = auth.getAuthorities().stream().map(a -> a.getAuthority())
                .filter(a -> a.startsWith("ROLE_")).findFirst().orElseThrow();
        return new CurrentMemberResponse(auth.getName(), role.substring("ROLE_".length()));
    }
}
