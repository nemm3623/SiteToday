package com.example.sitetoday.staffing.domain;

import java.util.List;
import java.util.UUID;

public interface OfficeRepository {
    List<Office> findAll();
    boolean exists(UUID id);
}
