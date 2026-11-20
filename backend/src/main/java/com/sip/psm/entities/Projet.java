package com.sip.psm.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "projets")
@Getter @Setter @NoArgsConstructor
public class Projet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String libelle;

    private Double budget;

    /** Duree en mois */
    private Integer duree;

    @Column(length = 2000)
    private String description;

    /** EN_ATTENTE | EN_COURS | TERMINE */
    @Column(length = 20)
    private String statut = "EN_COURS";

    private LocalDate dateCreation;

    /** Planification : debut et fin prevue (saisies par l'utilisateur) */
    private LocalDate dateDebut;
    private LocalDate dateFinPrevue;

    /** Avancement declare, de 0 a 100 % */
    private Integer avancement = 0;

    /** Budget deja consomme (DT), optionnel */
    private Double budgetConsomme;

    @PrePersist
    void onCreate() {
        if (dateCreation == null) dateCreation = LocalDate.now();
        if (statut == null) statut = "EN_COURS";
        if (dateDebut == null) dateDebut = dateCreation;
        if (avancement == null) avancement = 0;
    }

    // Many-to-One Projet -> Client (colonne id_client)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_client", nullable = false)
    private Client client;
}
