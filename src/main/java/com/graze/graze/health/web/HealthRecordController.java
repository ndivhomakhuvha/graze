package com.graze.graze.health.web;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/health-records")
public class HealthRecordController {

  @GetMapping
  @PreAuthorize("hasAnyRole('view-health', 'manage-health')")
  public void getHealthRecords() {
    // TODO: implement list health records
  }

  @PostMapping
  @PreAuthorize("hasRole('manage-health')")
  public void createHealthRecord() {
    // TODO: implement create health record
  }
}
