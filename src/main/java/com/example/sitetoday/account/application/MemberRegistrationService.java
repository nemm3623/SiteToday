package com.example.sitetoday.account.application;

import java.util.Objects;
import java.util.UUID;
import com.example.sitetoday.account.domain.Member;
import com.example.sitetoday.account.domain.MemberRepository;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class MemberRegistrationService {
    public record Command(String email, String password, Member.Role role) {
        public Command {
            if (email == null || email.isBlank() || password == null || password.isBlank()) {
                throw new IllegalArgumentException("Email and password are required");
            }
            Objects.requireNonNull(role);
        }
    }

    public static class EmailAlreadyRegistered extends RuntimeException { }

    public static class OfficeRegistrationRestricted extends RuntimeException { }

    private final MemberRepository memberRepository;
    private final PasswordEncoder encoder;

    public MemberRegistrationService(MemberRepository memberRepository, PasswordEncoder encoder) {
        this.memberRepository = memberRepository;
        this.encoder = encoder;
    }

    public UUID register(Command command) {
        if (command.role() == Member.Role.OFFICE) {
            throw new OfficeRegistrationRestricted();
        }
        Member member = new Member(UUID.randomUUID(), Member.normalizeEmail(command.email()),
                encoder.encode(command.password()), command.role());
        try {
            memberRepository.save(member);
        } catch (DuplicateKeyException duplicate) {
            throw new EmailAlreadyRegistered();
        }
        return member.id();
    }
}
