package com.graze.graze.animal.application;

import com.graze.graze.animal.domain.Animal;
import com.graze.graze.animal.domain.dto.AnimalDto;
import com.graze.graze.animal.domain.enums.AnimalType;
import com.graze.graze.animal.domain.enums.Gender;
import com.graze.graze.animal.domain.mapper.AnimalMapper;
import com.graze.graze.animal.domain.repository.AnimalRepository;
import com.graze.graze.animal.events.AnimalRegistered;
import com.graze.graze.common.exceptions.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

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
    resolveParents(animal, animalDto);
    Animal savedAnimal = animalRepository.save(animal);
    publisher.publishEvent(new AnimalRegistered(savedAnimal.getTagNo()));
    return animalMapper.toDto(savedAnimal);
  }

  public List<AnimalDto> findAll() {
    return animalMapper.toDtoList(animalRepository.findAll());
  }

  public AnimalDto findByTagNo(String tagNo) {
    Animal animal = animalRepository.findById(tagNo)
      .orElseThrow(() -> new ResourceNotFoundException("Animal not found with tag: " + tagNo));
    return animalMapper.toDto(animal);
  }

  public List<AnimalDto> findByType(AnimalType type) {
    return animalMapper.toDtoList(animalRepository.findByType(type));
  }

  public List<AnimalDto> findByGender(Gender gender) {
    return animalMapper.toDtoList(animalRepository.findByGender(gender));
  }

  public List<AnimalDto> findByTypeAndGender(AnimalType type, Gender gender) {
    return animalMapper.toDtoList(animalRepository.findByTypeAndGender(type, gender));
  }

  @Transactional
  public AnimalDto update(String tagNo, AnimalDto animalDto) {
    Animal animal = animalRepository.findById(tagNo)
      .orElseThrow(() -> new ResourceNotFoundException("Animal not found with tag: " + tagNo));
    animalMapper.updateAnimalFromDto(animalDto, animal);
    resolveParents(animal, animalDto);
    Animal saved = animalRepository.save(animal);
    return animalMapper.toDto(saved);
  }

  @Transactional
  public void delete(String tagNo) {
    if (!animalRepository.existsById(tagNo)) {
      throw new ResourceNotFoundException("Animal not found with tag: " + tagNo);
    }
    animalRepository.deleteById(tagNo);
  }

  public List<AnimalDto> findOffspring(String tagNo) {
    Animal animal = animalRepository.findById(tagNo)
      .orElseThrow(() -> new ResourceNotFoundException("Animal not found with tag: " + tagNo));

    List<Animal> offspring;
    if (animal.getGender() == Gender.FEMALE) {
      offspring = animalRepository.findByMotherTagNo(tagNo);
    } else {
      offspring = animalRepository.findByFatherTagNo(tagNo);
    }
    return animalMapper.toDtoList(offspring);
  }

  private void resolveParents(Animal animal, AnimalDto dto) {
    if (dto.getMotherTagNo() != null) {
      Animal mother = animalRepository.findById(dto.getMotherTagNo())
        .orElseThrow(() -> new ResourceNotFoundException("Mother not found with tag: " + dto.getMotherTagNo()));
      animal.setMother(mother);
    }
    if (dto.getFatherTagNo() != null) {
      Animal father = animalRepository.findById(dto.getFatherTagNo())
        .orElseThrow(() -> new ResourceNotFoundException("Father not found with tag: " + dto.getFatherTagNo()));
      animal.setFather(father);
    }
  }
}
