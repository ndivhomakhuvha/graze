package com.graze.graze.health.web;

import com.graze.graze.health.application.HealthService;
import com.graze.graze.health.domain.dto.HealthRecordDto;
import com.graze.graze.health.domain.enums.RecordStatus;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/health-records")
public class HealthRecordController {
  private final HealthService healthService;

  public HealthRecordController(HealthService healthService) {
    this.healthService = healthService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public HealthRecordDto create(@RequestBody HealthRecordDto dto) {
    return healthService.createRecord(dto);
  }

  @GetMapping
  public List<HealthRecordDto> findAll(@RequestParam(required = false) RecordStatus status) {
    if (status != null) {
      return healthService.findRecordsByStatus(status);
    }
    return healthService.findAllRecords();
  }

  @GetMapping("/{id}")
  public HealthRecordDto findById(@PathVariable Long id) {
    return healthService.findRecordById(id);
  }

  @GetMapping("/animal/{tagNo}")
  public List<HealthRecordDto> findByAnimal(@PathVariable String tagNo) {
    return healthService.findRecordsByAnimal(tagNo);
  }

  @GetMapping("/overdue")
  public List<HealthRecordDto> findOverdue() {
    return healthService.findOverdueRecords();
  }

  @GetMapping("/upcoming")
  public List<HealthRecordDto> findUpcoming(@RequestParam(defaultValue = "7") int daysAhead) {
    return healthService.findUpcomingRecords(daysAhead);
  }

  @PutMapping("/{id}")
  public HealthRecordDto update(@PathVariable Long id, @RequestBody HealthRecordDto dto) {
    return healthService.updateRecord(id, dto);
  }

  @PatchMapping("/{id}/complete")
  public HealthRecordDto complete(@PathVariable Long id) {
    return healthService.completeRecord(id);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    healthService.deleteRecord(id);
  }
}
