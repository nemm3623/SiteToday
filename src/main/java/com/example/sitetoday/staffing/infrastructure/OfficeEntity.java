package com.example.sitetoday.staffing.infrastructure;

import com.example.sitetoday.staffing.domain.Office;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "offices")
class OfficeEntity {
    @Id private UUID id;
    @Column(name = "member_id", nullable = false, unique = true)
    private UUID memberId;
    private String name;

    protected OfficeEntity() {}

    Office toDomain() {
        return new Office(id, memberId, name);
    }
}
