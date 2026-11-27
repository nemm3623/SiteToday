package com.example.sitetoday.staffing.infrastructure;

import com.example.sitetoday.staffing.domain.Office;
import com.example.sitetoday.staffing.domain.OfficeRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
class OfficeRepositoryImpl implements OfficeRepository {
    private final OfficeJpaRepository offices;

    OfficeRepositoryImpl(OfficeJpaRepository offices) {
        this.offices = offices;
    }

    public List<Office> findAll() {
        return offices.findAllByOrderByNameAscIdAsc().stream().map(OfficeEntity::toDomain).toList();
    }

    public boolean exists(UUID id) {
        return offices.existsById(id);
    }
}
