package com.graze.graze.animal.application;

import com.graze.graze.animal.domain.Animal;
import com.graze.graze.animal.domain.dto.AnimalDto;
import com.graze.graze.animal.domain.mapper.AnimalMapper;
import com.graze.graze.animal.events.AnimalRegistered;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class AnimalService {
  private final ApplicationEventPublisher publisher;
  private final AnimalMapper animalMapper;

  public AnimalService(ApplicationEventPublisher publisher, AnimalMapper animalMapper) {
    this.publisher = publisher;
    this.animalMapper = animalMapper;
  }

  public void register(AnimalDto animalDto) {
    Animal animal = animalMapper.toAnimal(animalDto);
    publisher.publishEvent(new AnimalRegistered(animal));
  }
}
