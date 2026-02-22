package com.graze.graze.health.domain.repository;

import com.graze.graze.health.domain.HealthRecord;
import com.graze.graze.health.domain.enums.RecordStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface HealthRecordRepository extends JpaRepository<HealthRecord, Long> {
  List<HealthRecord> findByAnimalTagNo(String animalTagNo);
  List<HealthRecord> findByStatus(RecordStatus status);
  List<HealthRecord> findByAnimalTagNoAndStatus(String animalTagNo, RecordStatus status);
  List<HealthRecord> findByNextDueBefore(LocalDate date);
  List<HealthRecord> findByNextDueBetween(LocalDate from, LocalDate to);
}
