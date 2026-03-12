package com.graze.graze.finances.domain;

import com.graze.graze.finances.domain.enums.FinanceCategory;
import com.graze.graze.finances.domain.enums.TransactionType;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
public class Finance {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TransactionType transactionType;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private FinanceCategory category;

  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal amount;

  @Column(nullable = false)
  private LocalDate date;

  private String description;

  @Column(name = "animal_tag_no")
  private String animalTagNo;

  @Column(name = "reference_id")
  private String referenceId;
}
