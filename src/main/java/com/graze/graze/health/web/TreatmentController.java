package com.graze.graze.health.web;

import com.graze.graze.health.application.HealthService;
import com.graze.graze.health.domain.dto.TreatmentDto;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/treatments")
public class TreatmentController {
  private final HealthService healthService;

  public TreatmentController(HealthService healthService) {
    this.healthService = healthService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasRole('manage-health')")
  public TreatmentDto create(@RequestBody TreatmentDto dto) {
    return healthService.createTreatment(dto);
  }

  @GetMapping
  @PreAuthorize("hasRole('view-health')")
  public List<TreatmentDto> findAll() {
    return healthService.findAllTreatments();
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasRole('view-health')")
  public TreatmentDto findById(@PathVariable Long id) {
    return healthService.findTreatmentById(id);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('manage-health')")
  public TreatmentDto update(@PathVariable Long id, @RequestBody TreatmentDto dto) {
    return healthService.updateTreatment(id, dto);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize("hasRole('manage-health')")
  public void delete(@PathVariable Long id) {
    healthService.deleteTreatment(id);
  }
}

