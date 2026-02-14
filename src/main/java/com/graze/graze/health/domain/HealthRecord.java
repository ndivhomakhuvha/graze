package com.graze.graze.health.domain;


import lombok.Data;

@Data
public class HealthRecord {
  private final String tagNo;
  private String status = "NEW";
}
