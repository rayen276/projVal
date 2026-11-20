package com.sip.psm.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;

public record ProjetRequest(
        @NotBlank(message = "Le libelle est obligatoire") String libelle,
        @NotNull(message = "Le budget est obligatoire") @PositiveOrZero(message = "Le budget doit etre positif") Double budget,
        @NotNull(message = "La duree est obligatoire") @Positive(message = "La duree doit etre positive") Integer duree,
        String description,
        @Pattern(regexp = "EN_ATTENTE|EN_COURS|TERMINE", message = "Statut invalide") String statut,
        LocalDate dateDebut,
        @NotNull(message = "La date de fin prevue est obligatoire") LocalDate dateFinPrevue,
        @Min(value = 0, message = "Avancement entre 0 et 100") @Max(value = 100, message = "Avancement entre 0 et 100") Integer avancement,
        @PositiveOrZero(message = "Le budget consomme doit etre positif") Double budgetConsomme,
        @NotNull(message = "Le client est obligatoire") Long clientId) {
}
