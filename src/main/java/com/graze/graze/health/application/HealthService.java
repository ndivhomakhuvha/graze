package com.graze.graze.health.application;

import com.graze.graze.animal.events.AnimalRegistered;
import org.springframework.stereotype.Service;

@Service
public class HealthService {
  public void createRecord(AnimalRegistered animalRegistered) {
    System.out.println("Health record created for " + animalRegistered.tagNo());
  }
}
