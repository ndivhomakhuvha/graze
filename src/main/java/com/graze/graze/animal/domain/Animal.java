package com.graze.graze.animal.domain;

import com.graze.graze.animal.domain.enums.AnimalStatus;
import com.graze.graze.animal.domain.enums.AnimalType;
import com.graze.graze.animal.domain.enums.Color;
import com.graze.graze.animal.domain.enums.Gender;
import com.graze.graze.animal.generators.AnimalTagId;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;

import lombok.Data;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Animal {
  @Id
  @AnimalTagId
  @Column(name = "tag_no")
  private String tagNo;
  private String name;
  @Enumerated(EnumType.STRING)
  private Color color;
  @Enumerated(EnumType.STRING)
  private AnimalType type;
  @Enumerated(EnumType.STRING)
  private AnimalStatus status;
  @Enumerated(EnumType.STRING)
  private Gender gender;

  private LocalDate dateOfBirth;

  private double birthWeight;
  private double currentWeight;

  // Parents
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "mother_id")
  @Nullable
  private Animal mother;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "father_id")
  @Nullable
  private Animal father;

  // Children
  @OneToMany(mappedBy = "mother")
  @Nullable
  private List<Animal> childrenFromMother = new ArrayList<>();


  @OneToMany(mappedBy = "father")
  @Nullable
  private List<Animal> childrenFromFather = new ArrayList<>();

  // Owners
  @OneToMany(mappedBy = "animal", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<AnimalOwners> owners = new ArrayList<>();

}
