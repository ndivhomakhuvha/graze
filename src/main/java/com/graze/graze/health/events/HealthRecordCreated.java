package com.graze.graze.health.events;

public record HealthRecordCreated(Long healthRecordId, String animalTagNo, String treatmentName, String dosage) {
}

