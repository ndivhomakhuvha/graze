package com.graze.graze.animal.domain.dto;

import com.graze.graze.animal.domain.enums.AnimalStatus;
import com.graze.graze.animal.domain.enums.AnimalType;
import com.graze.graze.animal.domain.enums.Color;
import com.graze.graze.animal.domain.enums.Gender;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class AnimalDto {
  private String tagNo;
  private String name;
  private Color color;
  private AnimalType type;
  private AnimalStatus status;
  private Gender gender;
  private LocalDate dateOfBirth;
  private double birthWeight;
  private double currentWeight;
  private String motherTagNo;
  private String fatherTagNo;
  private List<AnimalOwnerDto> owners;
}
