package com.example.sitetoday.account.application;

import java.util.Objects;
import java.util.UUID;
import com.example.sitetoday.account.domain.AccountErrorCode;
import com.example.sitetoday.common.exception.BusinessException;
import com.example.sitetoday.account.domain.Member;
import com.example.sitetoday.account.domain.MemberRepository;
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

    private final MemberRepository memberRepository;
    private final PasswordEncoder encoder;

    public MemberRegistrationService(MemberRepository memberRepository, PasswordEncoder encoder) {
        this.memberRepository = memberRepository;
        this.encoder = encoder;
    }

    public UUID register(Command command) {
        if (command.role() == Member.Role.OFFICE) {
            throw new BusinessException(AccountErrorCode.OFFICE_REGISTRATION_RESTRICTED);
        }
        Member member = new Member(UUID.randomUUID(), Member.normalizeEmail(command.email()),
                encoder.encode(command.password()), command.role());
        memberRepository.save(member);
        return member.id();
    }
}
