package com.example.sitetoday.account.infrastructure;

import com.example.sitetoday.account.domain.AccountErrorCode;
import com.example.sitetoday.common.exception.BusinessException;
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
                throw new BusinessException(AccountErrorCode.EMAIL_ALREADY_REGISTERED, exception);
            }
            throw exception;
        }
    }

    @Override
    public Optional<Member> findByEmail(String email) {
        return repository.findByEmail(email).map(MemberEntity::toDomain);
    }
}
