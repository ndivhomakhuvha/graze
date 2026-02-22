package com.graze.graze.health.events;

import org.springframework.modulith.NamedInterface;

import java.math.BigDecimal;

@NamedInterface("TreatmentAdministered")
public record TreatmentAdministered(
  Long healthRecordId,
  String animalTagNo,
  String treatmentName,
  String dosage,
  BigDecimal cost
) {
}

