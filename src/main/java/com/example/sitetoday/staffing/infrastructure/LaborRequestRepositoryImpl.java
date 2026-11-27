package com.example.sitetoday.staffing.infrastructure;

import com.example.sitetoday.staffing.domain.LaborRequest;
import com.example.sitetoday.staffing.domain.LaborRequestRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

@Repository
class LaborRequestRepositoryImpl implements LaborRequestRepository {
    private final EntityManager entities;

    LaborRequestRepositoryImpl(EntityManager entities) {
        this.entities = entities;
    }

    public void save(LaborRequest request) {
        entities.persist(new LaborRequestEntity(request));
    }
}
