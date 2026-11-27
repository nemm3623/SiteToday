package com.example.sitetoday.staffing.infrastructure;

import java.util.List;
import java.util.UUID;
import org.springframework.data.repository.Repository;

interface OfficeJpaRepository extends Repository<OfficeEntity, UUID> {
    List<OfficeEntity> findAllByOrderByNameAscIdAsc();
    boolean existsById(UUID id);
}
