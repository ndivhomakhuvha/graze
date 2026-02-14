package com.graze.graze.health.domain;


import com.graze.graze.animal.domain.Animal;
import com.graze.graze.health.domain.enums.RecordStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class HealthRecord {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;


  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
    name = "animal_tag_no",           // FK column in health_record table
    referencedColumnName = "tagNo",   // PK in Animal
    nullable = false
  )
  private Animal animal;

  private LocalDate date;

  @ManyToOne
  private Treatment treatment;

  private String dosage;

  private LocalDate nextDue;

  private String notes;

  @Enumerated(EnumType.STRING)
  private RecordStatus status = RecordStatus.NEW;

  public boolean isOverdue() {
    return nextDue != null && LocalDate.now().isAfter(nextDue);
  }

}
