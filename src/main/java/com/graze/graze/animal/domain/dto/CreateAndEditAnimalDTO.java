package com.graze.graze.animal.domain.dto;

import com.graze.graze.animal.domain.enums.AnimalType;
import com.graze.graze.animal.domain.enums.Color;
import com.graze.graze.animal.domain.enums.Gender;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CreateAndEditAnimalDTO {
  private String name;
  private Color color;
  private AnimalType type;
  private Gender gender;
  private LocalDate dateOfBirth;
  private double birthWeight;
  private double currentWeight;
  private String motherTagNo;
  private String fatherTagNo;
  private List<AnimalOwnerDto> owners;
}
