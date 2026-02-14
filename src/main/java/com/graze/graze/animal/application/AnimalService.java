package com.graze.graze.animal.application;

import com.graze.graze.animal.domain.Animal;
import com.graze.graze.animal.events.AnimalRegistered;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class AnimalService {
  private final ApplicationEventPublisher publisher;

  public AnimalService(ApplicationEventPublisher publisher) {
    this.publisher = publisher;
  }

  public void register(Animal animal) {
    publisher.publishEvent(new AnimalRegistered(animal));
  }
}
