package com.graze.graze.health.listeners;

import com.graze.graze.animal.events.AnimalRegistered;
import com.graze.graze.health.application.HealthService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class AnimalRegisteredListeners {
  private final HealthService healthService;

  public AnimalRegisteredListeners(HealthService healthService) {
    this.healthService = healthService;
  }

  @EventListener
  void on(AnimalRegistered event) {
    healthService.createInitialRecord(event.tagNo());
  }
}
