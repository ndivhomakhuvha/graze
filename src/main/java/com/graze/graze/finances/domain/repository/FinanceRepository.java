package com.graze.graze.finances.domain.repository;

import com.graze.graze.finances.domain.Finance;
import com.graze.graze.finances.domain.enums.FinanceCategory;
import com.graze.graze.finances.domain.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface FinanceRepository extends JpaRepository<Finance, Long> {
  List<Finance> findByTransactionType(TransactionType transactionType);
  List<Finance> findByCategory(FinanceCategory category);
  List<Finance> findByAnimalTagNo(String animalTagNo);
  List<Finance> findByDateBetween(LocalDate from, LocalDate to);
  List<Finance> findByTransactionTypeAndDateBetween(TransactionType type, LocalDate from, LocalDate to);

  @Query("SELECT COALESCE(SUM(f.amount), 0) FROM Finance f WHERE f.transactionType = :type")
  BigDecimal sumByTransactionType(TransactionType type);

  @Query("SELECT COALESCE(SUM(f.amount), 0) FROM Finance f WHERE f.transactionType = :type AND f.date BETWEEN :from AND :to")
  BigDecimal sumByTransactionTypeAndDateBetween(TransactionType type, LocalDate from, LocalDate to);

  @Query("SELECT COALESCE(SUM(f.amount), 0) FROM Finance f WHERE f.animalTagNo = :tagNo AND f.transactionType = :type")
  BigDecimal sumByAnimalAndTransactionType(String tagNo, TransactionType type);
}


