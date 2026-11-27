package com.example.sitetoday.account.infrastructure;

import com.example.sitetoday.account.domain.Member;
import com.example.sitetoday.account.domain.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
class MemberUserDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        Member member = memberRepository.findByEmail(Member.normalizeEmail(email))
                .orElseThrow(() -> new UsernameNotFoundException("Unknown member"));
        return User.withUsername(member.id().toString()).password(member.passwordHash())
                .roles(member.role().name()).build();
    }
}
