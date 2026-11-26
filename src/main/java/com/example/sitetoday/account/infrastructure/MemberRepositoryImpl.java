package com.example.sitetoday.account.infrastructure;

import org.springframework.dao.DuplicateKeyException;
import com.example.sitetoday.account.domain.Member;
import com.example.sitetoday.account.domain.MemberRepository;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

@Repository
class MemberRepositoryImpl implements MemberRepository {
    private final MemberJpaRepository repository;

    MemberRepositoryImpl(MemberJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(Member member) {
        try {
            repository.saveAndFlush(new MemberEntity(member));
        } catch (DataIntegrityViolationException exception) {
            if (repository.existsByEmail(member.email())) {
                throw new DuplicateKeyException("Email already registered", exception);
            }
            throw exception;
        }
    }

    @Override
    public Optional<Member> findByEmail(String email) {
        return repository.findByEmail(email).map(MemberEntity::toDomain);
    }
}
