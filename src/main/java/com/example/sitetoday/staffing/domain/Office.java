package com.example.sitetoday.staffing.domain;

import java.util.UUID;

public record Office(UUID id, UUID memberId, String name) {}
