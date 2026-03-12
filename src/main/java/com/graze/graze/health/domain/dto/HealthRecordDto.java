package com.graze.graze.health.domain.dto;

import com.graze.graze.health.domain.enums.RecordStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class HealthRecordDto {
  private Long id;
  private String animalTagNo;
  private LocalDate date;
  private Long treatmentId;
  private String treatmentName;
  private String dosage;
  private LocalDate nextDue;
  private String notes;
  private RecordStatus status;
  private boolean overdue;
}

