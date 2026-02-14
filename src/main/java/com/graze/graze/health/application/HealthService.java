package com.graze.graze.health.application;

import com.graze.graze.animal.events.AnimalRegistered;
import com.graze.graze.health.domain.HealthRecord;
import org.springframework.stereotype.Service;

@Service
public class HealthService {
  public void createRecord(AnimalRegistered animalRegistered) {
    HealthRecord record = new HealthRecord(animalRegistered.tagNo());
    System.out.println("Health record created for " + record);
  }
}
