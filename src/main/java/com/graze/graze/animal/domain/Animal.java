package com.graze.graze.animal.domain;

import com.graze.graze.animal.generators.AnimalTagId;
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
  private String tagNo;
  private String name;
  @Enumerated(EnumType.STRING)
  private Color color;
  @Enumerated(EnumType.STRING)
  private AnimalType type;
  @Enumerated(EnumType.STRING)
  private Gender gender;

  private LocalDate dateOfBirth;

  private double birthWeight;
  private double currentWeight;

  // Parents
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "mother_id")
  private Animal mother;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "father_id")
  private Animal father;

  // Children
  @OneToMany(mappedBy = "mother")
  private List<Animal> childrenFromMother = new ArrayList<>();

  @OneToMany(mappedBy = "father")
  private List<Animal> childrenFromFather = new ArrayList<>();

  public void setParents(Animal mother, Animal father) {
    this.mother = mother;
    this.father = father;

    if (mother != null) mother.childrenFromMother.add(this);
    if (father != null) father.childrenFromFather.add(this);
  }
}
