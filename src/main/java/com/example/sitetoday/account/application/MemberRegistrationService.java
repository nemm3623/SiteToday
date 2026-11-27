package com.example.sitetoday.account.application;

import java.util.UUID;
import com.example.sitetoday.account.domain.AccountErrorCode;
import com.example.sitetoday.common.exception.BusinessException;
import com.example.sitetoday.account.domain.Member;
import com.example.sitetoday.account.domain.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MemberRegistrationService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder encoder;

    public RegisterMemberResult register(RegisterMemberCommand command) {
        if (command.role() == Member.Role.OFFICE) {
            throw new BusinessException(AccountErrorCode.OFFICE_REGISTRATION_RESTRICTED);
        }
        Member member = new Member(UUID.randomUUID(), Member.normalizeEmail(command.email()),
                encoder.encode(command.password()), command.role());
        memberRepository.save(member);
        return new RegisterMemberResult(member.id());
    }
}
