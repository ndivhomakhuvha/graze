package com.graze.graze.animal.application;

import com.graze.graze.animal.domain.Animal;
import com.graze.graze.animal.domain.dto.AnimalDto;
import com.graze.graze.animal.domain.mapper.AnimalMapper;
import com.graze.graze.animal.domain.repository.AnimalRepository;
import com.graze.graze.animal.events.AnimalRegistered;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class AnimalService {
  private final ApplicationEventPublisher publisher;
  private final AnimalMapper animalMapper;
  private final AnimalRepository animalRepository;

  public AnimalService(ApplicationEventPublisher publisher, AnimalMapper animalMapper, AnimalRepository animalRepository) {
    this.publisher = publisher;
    this.animalMapper = animalMapper;
    this.animalRepository = animalRepository;
  }

  @Transactional
  public AnimalDto register(AnimalDto animalDto) {
    Animal animal = animalMapper.toAnimal(animalDto);
    Animal savedAnimal = animalRepository.save(animal);
    publisher.publishEvent(new AnimalRegistered(savedAnimal.getTagNo()));
    return animalMapper.toDto(savedAnimal);
  }
}
