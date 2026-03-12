package com.graze.graze.animal.domain.interfaces;

import com.graze.graze.animal.domain.dto.AnimalDto;
import com.graze.graze.animal.domain.dto.CreateAndEditAnimalDTO;
import com.graze.graze.animal.domain.enums.AnimalType;
import com.graze.graze.animal.domain.enums.Gender;

import java.util.List;

public interface AnimalInterface {
  AnimalDto register(CreateAndEditAnimalDTO createAndEditAnimalDTO);

  List<AnimalDto> findAll();

  AnimalDto findByTagNo(String tagNo);

  List<AnimalDto> findByType(AnimalType type);

  List<AnimalDto> findByGender(Gender gender);

  List<AnimalDto> findByTypeAndGender(AnimalType type, Gender gender);

  AnimalDto update(String tagNo, CreateAndEditAnimalDTO createAndEditAnimalDTO);

  void delete(String tagNo);

  List<AnimalDto> findOffspring(String tagNo);
}
