package com.graze.graze.health.domain.repository;

import com.graze.graze.health.domain.HealthRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HealthRecordRepository extends JpaRepository<HealthRecord, Long> {
}
