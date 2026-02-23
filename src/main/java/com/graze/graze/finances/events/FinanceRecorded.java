package com.graze.graze.finances.events;

import com.graze.graze.finances.domain.enums.FinanceCategory;
import com.graze.graze.finances.domain.enums.TransactionType;
import org.springframework.modulith.NamedInterface;

import java.math.BigDecimal;

@NamedInterface("FinanceRecorded")
public record FinanceRecorded(
  Long financeId,
  TransactionType transactionType,
  FinanceCategory category,
  BigDecimal amount,
  String animalTagNo,
  String description
) {
}

