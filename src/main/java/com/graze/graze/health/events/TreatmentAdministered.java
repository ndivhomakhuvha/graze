package com.graze.graze.health.events;

import java.math.BigDecimal;

public record TreatmentAdministered(
  Long healthRecordId,
  String animalTagNo,
  String treatmentName,
  String dosage,
  BigDecimal cost
) {
}

