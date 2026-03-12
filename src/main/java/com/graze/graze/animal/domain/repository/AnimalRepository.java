package com.graze.graze.animal.domain.repository;

import com.graze.graze.animal.domain.Animal;
import com.graze.graze.animal.domain.enums.AnimalType;
import com.graze.graze.animal.domain.enums.Gender;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnimalRepository extends JpaRepository<Animal, String> {
  List<Animal> findByType(AnimalType type);
  List<Animal> findByGender(Gender gender);
  List<Animal> findByTypeAndGender(AnimalType type, Gender gender);
  List<Animal> findByMotherTagNo(String motherTagNo);
  List<Animal> findByFatherTagNo(String fatherTagNo);
}
