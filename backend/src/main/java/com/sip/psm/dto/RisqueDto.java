package com.sip.psm.dto;

import java.time.LocalDate;

/** Resultat de l'analyse de risque d'un projet (calcule a chaque lecture, jamais stocke). */
public record RisqueDto(String niveau, int score, long joursRestants, boolean enRetard, double pctTemps,
                        LocalDate dateFinEstimee, Long retardEstimeJours, String message) {
}
