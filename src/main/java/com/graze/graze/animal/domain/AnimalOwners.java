package com.graze.graze.animal.domain;

import com.graze.graze.user.domain.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "animal_owners")
@IdClass(AnimalOwnerId.class)
@Data
public class AnimalOwners {

    @Id
    @Column(name = "animal_tag_no", nullable = false)
    private String animalTagNo;

    @Id
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false, length = 20)
    private String role;

    @Column(name = "owned_since", nullable = false)
    private LocalDate ownedSince;

    // ── Relationships ──

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "animal_tag_no", insertable = false, updatable = false)
    private Animal animal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;
}
