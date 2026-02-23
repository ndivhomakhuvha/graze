package com.graze.graze.finances.application;

import com.graze.graze.common.exceptions.ResourceNotFoundException;
import com.graze.graze.finances.domain.Finance;
import com.graze.graze.finances.domain.dto.FinanceDto;
import com.graze.graze.finances.domain.dto.FinanceSummaryDto;
import com.graze.graze.finances.domain.enums.FinanceCategory;
import com.graze.graze.finances.domain.enums.TransactionType;
import com.graze.graze.finances.domain.mapper.FinanceMapper;
import com.graze.graze.finances.domain.repository.FinanceRepository;
import com.graze.graze.finances.events.FinanceRecorded;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class FinanceService {
  private final FinanceRepository financeRepository;
  private final FinanceMapper financeMapper;
  private final ApplicationEventPublisher publisher;

  public FinanceService(FinanceRepository financeRepository,
                        FinanceMapper financeMapper,
                        ApplicationEventPublisher publisher) {
    this.financeRepository = financeRepository;
    this.financeMapper = financeMapper;
    this.publisher = publisher;
  }

  @Transactional
  public FinanceDto create(FinanceDto dto) {
    Finance finance = financeMapper.toEntity(dto);
    if (finance.getDate() == null) {
      finance.setDate(LocalDate.now());
    }
    Finance saved = financeRepository.save(finance);

    publisher.publishEvent(new FinanceRecorded(
      saved.getId(),
      saved.getTransactionType(),
      saved.getCategory(),
      saved.getAmount(),
      saved.getAnimalTagNo(),
      saved.getDescription()
    ));

    return financeMapper.toDto(saved);
  }

  /**
   * Called by event listeners to automatically record expenses (e.g., treatment costs).
   */
  @Transactional
  public FinanceDto recordExpense(FinanceCategory category, BigDecimal amount,
                                  String description, String animalTagNo, String referenceId) {
    Finance finance = new Finance();
    finance.setTransactionType(TransactionType.EXPENSE);
    finance.setCategory(category);
    finance.setAmount(amount);
    finance.setDate(LocalDate.now());
    finance.setDescription(description);
    finance.setAnimalTagNo(animalTagNo);
    finance.setReferenceId(referenceId);

    Finance saved = financeRepository.save(finance);
    return financeMapper.toDto(saved);
  }

  public List<FinanceDto> findAll() {
    return financeMapper.toDtoList(financeRepository.findAll());
  }

  public FinanceDto findById(Long id) {
    Finance finance = financeRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Finance record not found with id: " + id));
    return financeMapper.toDto(finance);
  }

  public List<FinanceDto> findByTransactionType(TransactionType type) {
    return financeMapper.toDtoList(financeRepository.findByTransactionType(type));
  }

  public List<FinanceDto> findByCategory(FinanceCategory category) {
    return financeMapper.toDtoList(financeRepository.findByCategory(category));
  }

  public List<FinanceDto> findByAnimal(String animalTagNo) {
    return financeMapper.toDtoList(financeRepository.findByAnimalTagNo(animalTagNo));
  }

  public List<FinanceDto> findByDateRange(LocalDate from, LocalDate to) {
    return financeMapper.toDtoList(financeRepository.findByDateBetween(from, to));
  }

  @Transactional
  public FinanceDto update(Long id, FinanceDto dto) {
    Finance finance = financeRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Finance record not found with id: " + id));
    financeMapper.updateFromDto(dto, finance);
    Finance saved = financeRepository.save(finance);
    return financeMapper.toDto(saved);
  }

  @Transactional
  public void delete(Long id) {
    if (!financeRepository.existsById(id)) {
      throw new ResourceNotFoundException("Finance record not found with id: " + id);
    }
    financeRepository.deleteById(id);
  }

  public FinanceSummaryDto getSummary() {
    BigDecimal totalIncome = financeRepository.sumByTransactionType(TransactionType.INCOME);
    BigDecimal totalExpense = financeRepository.sumByTransactionType(TransactionType.EXPENSE);

    FinanceSummaryDto summary = new FinanceSummaryDto();
    summary.setTotalIncome(totalIncome);
    summary.setTotalExpense(totalExpense);
    summary.setNetBalance(totalIncome.subtract(totalExpense));
    return summary;
  }

  public FinanceSummaryDto getSummaryForPeriod(LocalDate from, LocalDate to) {
    BigDecimal totalIncome = financeRepository.sumByTransactionTypeAndDateBetween(TransactionType.INCOME, from, to);
    BigDecimal totalExpense = financeRepository.sumByTransactionTypeAndDateBetween(TransactionType.EXPENSE, from, to);

    FinanceSummaryDto summary = new FinanceSummaryDto();
    summary.setTotalIncome(totalIncome);
    summary.setTotalExpense(totalExpense);
    summary.setNetBalance(totalIncome.subtract(totalExpense));
    return summary;
  }

  public FinanceSummaryDto getAnimalSummary(String animalTagNo) {
    BigDecimal totalIncome = financeRepository.sumByAnimalAndTransactionType(animalTagNo, TransactionType.INCOME);
    BigDecimal totalExpense = financeRepository.sumByAnimalAndTransactionType(animalTagNo, TransactionType.EXPENSE);

    FinanceSummaryDto summary = new FinanceSummaryDto();
    summary.setTotalIncome(totalIncome);
    summary.setTotalExpense(totalExpense);
    summary.setNetBalance(totalIncome.subtract(totalExpense));
    return summary;
  }
}
