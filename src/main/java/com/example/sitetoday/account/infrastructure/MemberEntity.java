package com.example.sitetoday.account.infrastructure;

import com.example.sitetoday.account.domain.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "members")
class MemberEntity {
    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 254)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 20)
    private Member.Role role;

    protected MemberEntity() { }

    MemberEntity(Member member) {
        id = member.id();
        email = member.email();
        passwordHash = member.passwordHash();
        role = member.role();
    }

    Member toDomain() {
        return new Member(id, email, passwordHash, role);
    }
}
