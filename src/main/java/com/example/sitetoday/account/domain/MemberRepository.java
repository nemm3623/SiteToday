package com.example.sitetoday.account.domain;

import java.util.Optional;

public interface MemberRepository {
    void save(Member member);
    Optional<Member> findByEmail(String email);
}
