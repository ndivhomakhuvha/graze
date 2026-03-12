package com.graze.graze.finances.domain.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FinanceSummaryDto {
  private BigDecimal totalIncome;
  private BigDecimal totalExpense;
  private BigDecimal netBalance;
}

