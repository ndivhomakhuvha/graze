package com.graze.graze.health.events;

import org.springframework.modulith.NamedInterface;

@NamedInterface("HealthRecordCreated")
public record HealthRecordCreated(Long healthRecordId, String animalTagNo, String treatmentName, String dosage) {
}

