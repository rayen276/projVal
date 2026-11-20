package com.sip.psm.dto;

import java.util.List;

public record StatsDto(long totalClients, long totalProjets, double budgetTotal, double budgetMoyen,
                       List<ClientStat> topClients, List<StatutStat> parStatut, List<MoisStat> parMois,
                       List<RisqueStat> parRisque, List<ProjetRisque> projetsARisque) {

    public record ClientStat(String clientNom, long nbProjets, double budget) {
    }

    /** Nombre de projets par statut (EN_ATTENTE, EN_COURS, TERMINE). */
    public record StatutStat(String statut, long nb) {
    }

    /** Evolution mensuelle : mois au format yyyy-MM. */
    public record MoisStat(String mois, long nb, double budget) {
    }

    /** Nombre de projets par niveau de risque (FAIBLE, MOYEN, ELEVE, CRITIQUE, NON_EVALUE). */
    public record RisqueStat(String niveau, long nb) {
    }

    /** Projet a surveiller (risque eleve ou critique), tries du plus risque au moins risque. */
    public record ProjetRisque(Long id, String libelle, String clientNom, String niveau, int score,
                               long joursRestants, int avancement, String message) {
    }
}
