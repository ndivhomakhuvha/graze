package com.graze.graze.animal.domain.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class AnimalOwnerDto {
    private UUID userId;
    private String role;
    private LocalDate ownedSince;
}

