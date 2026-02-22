package com.graze.graze.finances.listeners;

import com.graze.graze.finances.application.FinanceService;
import com.graze.graze.finances.domain.enums.FinanceCategory;
import com.graze.graze.health.events.TreatmentAdministered;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class TreatmentAdministeredListener {
  private final FinanceService financeService;

  public TreatmentAdministeredListener(FinanceService financeService) {
    this.financeService = financeService;
  }

  @EventListener
  void on(TreatmentAdministered event) {
    if (event.cost() != null && event.cost().signum() > 0) {
      String description = String.format("Treatment: %s | Dosage: %s | Animal: %s",
        event.treatmentName(),
        event.dosage() != null ? event.dosage() : "N/A",
        event.animalTagNo());

      financeService.recordExpense(
        FinanceCategory.VETERINARY,
        event.cost(),
        description,
        event.animalTagNo(),
        "HEALTH-" + event.healthRecordId()
      );
    }
  }
}

