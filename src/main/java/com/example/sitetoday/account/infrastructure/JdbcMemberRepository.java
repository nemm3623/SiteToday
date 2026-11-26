package com.example.sitetoday.account.infrastructure;

import java.util.Optional;
import java.util.UUID;
import com.example.sitetoday.account.domain.Member;
import com.example.sitetoday.account.domain.MemberRepository;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
class JdbcMemberRepository implements MemberRepository {
    private final JdbcClient db;

    JdbcMemberRepository(JdbcClient db) {
        this.db = db;
    }

    public void save(Member member) {
        db.sql("insert into members (id, email, password_hash, role) values (?, ?, ?, ?)")
                .params(member.id(), member.email(), member.passwordHash(), member.role().name()).update();
    }

    public Optional<Member> findByEmail(String email) {
        return db.sql("select id, email, password_hash, role from members where email = ?").param(email)
                .query((rs, row) -> new Member(rs.getObject("id", UUID.class), rs.getString("email"),
                        rs.getString("password_hash"), Member.Role.valueOf(rs.getString("role")))).optional();
    }
}
