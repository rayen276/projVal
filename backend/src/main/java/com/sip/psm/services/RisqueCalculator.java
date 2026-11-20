package com.sip.psm.services;

import com.sip.psm.dto.RisqueDto;
import com.sip.psm.entities.Projet;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Calcule le risque d'un projet en comparant le TEMPS ECOULE a l'AVANCEMENT declare (et au budget consomme).
 *
 *   pctTemps = jours ecoules / duree planifiee
 *   ecart    = pctTemps - avancement          (si positif : le projet est en retard sur son planning)
 *   score    = ecart + 0,5 x ecart budgetaire (+15 si fin dans moins de 7 jours et avancement < 80 %)
 *              (85 minimum si la date de fin est depassee)
 *   niveau   : < 20 FAIBLE | < 40 MOYEN | < 70 ELEVE | >= 70 CRITIQUE
 * Date de fin estimee : au rythme actuel (avancement / jours ecoules).
 */
public final class RisqueCalculator {

    private RisqueCalculator() { }

    public static RisqueDto calculer(Projet p, LocalDate today) {
        int av = p.getAvancement() == null ? 0 : p.getAvancement();
        String statut = p.getStatut() == null ? "EN_COURS" : p.getStatut();
        LocalDate fin = p.getDateFinPrevue();
        LocalDate debut = p.getDateDebut() != null ? p.getDateDebut() : p.getDateCreation();

        if ("TERMINE".equals(statut) || av >= 100) {
            return new RisqueDto("FAIBLE", 0, fin == null ? 0 : ChronoUnit.DAYS.between(today, fin), false, 100,
                    null, null, "Projet termine.");
        }
        if (fin == null || debut == null) {
            return new RisqueDto("NON_EVALUE", 0, 0, false, 0, null, null,
                    "Date de fin prevue non renseignee : risque non evalue.");
        }

        long total = Math.max(1, ChronoUnit.DAYS.between(debut, fin));
        long ecoules = Math.max(0, ChronoUnit.DAYS.between(debut, today));
        long restants = ChronoUnit.DAYS.between(today, fin);
        boolean enRetard = restants < 0;
        double pctTemps = Math.round(ecoules * 1000.0 / total) / 10.0;

        double score = Math.max(0, pctTemps - av);
        if (p.getBudget() != null && p.getBudget() > 0 && p.getBudgetConsomme() != null) {
            double pctBudget = p.getBudgetConsomme() / p.getBudget() * 100;
            score += 0.5 * Math.max(0, pctBudget - av);
        }
        if (!enRetard && restants <= 7 && av < 80) score += 15;
        if ("EN_ATTENTE".equals(statut) && ecoules > 0) score += 15;
        if (enRetard) score = Math.max(score, 85);
        int s = (int) Math.round(Math.min(100, score));

        String niveau = s < 20 ? "FAIBLE" : s < 40 ? "MOYEN" : s < 70 ? "ELEVE" : "CRITIQUE";

        LocalDate finEstimee = null;
        Long retardEstime = null;
        if (av > 0 && ecoules > 0) {
            long joursNecessaires = (long) Math.ceil(ecoules * 100.0 / av);
            finEstimee = debut.plusDays(joursNecessaires);
            retardEstime = ChronoUnit.DAYS.between(fin, finEstimee);
        }

        String message;
        if (enRetard) {
            message = "En retard de " + (-restants) + " jour(s) : replanifier ou renforcer l'equipe.";
        } else if ("FAIBLE".equals(niveau)) {
            message = "Projet dans les temps.";
        } else {
            message = "Avancement (" + av + " %) en dessous du temps ecoule (" + Math.round(pctTemps) + " %)";
            if (retardEstime != null && retardEstime > 0) {
                message += " : fin estimee avec " + retardEstime + " jour(s) de retard.";
            } else {
                message += ".";
            }
        }
        return new RisqueDto(niveau, s, restants, enRetard, pctTemps, finEstimee, retardEstime, message);
    }
}
