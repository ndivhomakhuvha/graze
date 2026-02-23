package com.graze.graze.finances.web;

import com.graze.graze.finances.application.FinanceService;
import com.graze.graze.finances.domain.dto.FinanceDto;
import com.graze.graze.finances.domain.dto.FinanceSummaryDto;
import com.graze.graze.finances.domain.enums.FinanceCategory;
import com.graze.graze.finances.domain.enums.TransactionType;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/finances")
public class FinanceController {
  private final FinanceService financeService;

  public FinanceController(FinanceService financeService) {
    this.financeService = financeService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasRole('manage-finances')")
  public FinanceDto create(@RequestBody FinanceDto dto) {
    return financeService.create(dto);
  }

  @GetMapping
  @PreAuthorize("hasRole('view-finances')")
  public List<FinanceDto> findAll(
    @RequestParam(required = false) TransactionType type,
    @RequestParam(required = false) FinanceCategory category
  ) {
    if (type != null) {
      return financeService.findByTransactionType(type);
    } else if (category != null) {
      return financeService.findByCategory(category);
    }
    return financeService.findAll();
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasRole('view-finances')")
  public FinanceDto findById(@PathVariable Long id) {
    return financeService.findById(id);
  }

  @GetMapping("/animal/{tagNo}")
  @PreAuthorize("hasRole('view-finances')")
  public List<FinanceDto> findByAnimal(@PathVariable String tagNo) {
    return financeService.findByAnimal(tagNo);
  }

  @GetMapping("/date-range")
  @PreAuthorize("hasRole('view-finances')")
  public List<FinanceDto> findByDateRange(
    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
  ) {
    return financeService.findByDateRange(from, to);
  }

  @GetMapping("/summary")
  @PreAuthorize("hasRole('view-finances')")
  public FinanceSummaryDto getSummary() {
    return financeService.getSummary();
  }

  @GetMapping("/summary/period")
  @PreAuthorize("hasRole('view-finances')")
  public FinanceSummaryDto getSummaryForPeriod(
    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
  ) {
    return financeService.getSummaryForPeriod(from, to);
  }

  @GetMapping("/summary/animal/{tagNo}")
  @PreAuthorize("hasRole('view-finances')")
  public FinanceSummaryDto getAnimalSummary(@PathVariable String tagNo) {
    return financeService.getAnimalSummary(tagNo);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('manage-finances')")
  public FinanceDto update(@PathVariable Long id, @RequestBody FinanceDto dto) {
    return financeService.update(id, dto);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize("hasRole('manage-finances')")
  public void delete(@PathVariable Long id) {
    financeService.delete(id);
  }
}

