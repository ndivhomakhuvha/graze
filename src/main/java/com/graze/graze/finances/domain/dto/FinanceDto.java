package com.graze.graze.finances.domain.dto;

import com.graze.graze.finances.domain.enums.FinanceCategory;
import com.graze.graze.finances.domain.enums.TransactionType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class FinanceDto {
  private Long id;
  private TransactionType transactionType;
  private FinanceCategory category;
  private BigDecimal amount;
  private LocalDate date;
  private String description;
  private String animalTagNo;
  private String referenceId;
}

