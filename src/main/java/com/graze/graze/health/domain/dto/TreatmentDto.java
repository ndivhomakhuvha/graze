package com.graze.graze.health.domain.dto;

import lombok.Data;

@Data
public class TreatmentDto {
  private Long id;
  private String name;
  private int defaultIntervalDays;
}

