package com.sip.psm.dto;

import com.sip.psm.entities.Projet;
import com.sip.psm.services.RisqueCalculator;

import java.time.LocalDate;

public record ProjetDto(Long id, String libelle, Double budget, Integer duree, String description,
                        String statut, LocalDate dateCreation, LocalDate dateDebut, LocalDate dateFinPrevue,
                        Integer avancement, Double budgetConsomme, RisqueDto risque,
                        Long clientId, String clientNom) {

    public static ProjetDto from(Projet p) {
        return new ProjetDto(p.getId(), p.getLibelle(), p.getBudget(), p.getDuree(), p.getDescription(),
                p.getStatut() == null ? "EN_COURS" : p.getStatut(), p.getDateCreation(),
                p.getDateDebut() != null ? p.getDateDebut() : p.getDateCreation(), p.getDateFinPrevue(),
                p.getAvancement() == null ? 0 : p.getAvancement(), p.getBudgetConsomme(),
                RisqueCalculator.calculer(p, LocalDate.now()),
                p.getClient().getId(), p.getClient().getNom());
    }
}
