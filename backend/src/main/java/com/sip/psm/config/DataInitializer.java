package com.sip.psm.config;

import com.sip.psm.entities.Client;
import com.sip.psm.entities.Projet;
import com.sip.psm.entities.Role;
import com.sip.psm.entities.User;
import com.sip.psm.repositories.ClientRepository;
import com.sip.psm.repositories.ProjetRepository;
import com.sip.psm.repositories.RoleRepository;
import com.sip.psm.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

/** Cree au demarrage les roles, un compte admin et (optionnel) quelques donnees de demonstration. */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final RoleRepository roleRepo;
    private final UserRepository userRepo;
    private final ClientRepository clientRepo;
    private final ProjetRepository projetRepo;
    private final PasswordEncoder encoder;

    @Value("${app.admin.email}") private String adminEmail;
    @Value("${app.admin.password}") private String adminPassword;
    @Value("${app.seed-demo-data:true}") private boolean seedDemo;

    public DataInitializer(RoleRepository roleRepo, UserRepository userRepo, ClientRepository clientRepo,
                           ProjetRepository projetRepo, PasswordEncoder encoder) {
        this.roleRepo = roleRepo;
        this.userRepo = userRepo;
        this.clientRepo = clientRepo;
        this.projetRepo = projetRepo;
        this.encoder = encoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        Role admin = roleRepo.findByLibelle(Role.ADMIN).orElseGet(() -> roleRepo.save(new Role(Role.ADMIN)));
        roleRepo.findByLibelle(Role.COLLABORATEUR).orElseGet(() -> roleRepo.save(new Role(Role.COLLABORATEUR)));

        if (!userRepo.existsByEmailIgnoreCase(adminEmail)) {
            User u = new User();
            u.setEmail(adminEmail);
            u.setPassword(encoder.encode(adminPassword));
            u.setNom("Admin");
            u.setPrenom("PSM");
            u.setTel("00000000");
            u.setRoles(new HashSet<>(Set.of(admin)));
            userRepo.save(u);
            log.info("Compte administrateur cree : {}", adminEmail);
        }

        if (seedDemo && clientRepo.count() == 0) {
            Client c1 = client("SmartSoft Solutions", "71 123 456", "contact@smartsoft.tn", "Tunis, Les Berges du Lac");
            Client c2 = client("Tunisie Telecom", "71 000 111", "projets@tt.tn", "Tunis, Montplaisir");
            Client c3 = client("Delice Danone", "71 222 333", "it@delice.tn", "Ben Arous");
            //     client, libelle, budget, duree, description, statut, creation(mois), debut(il y a j jours), fin(dans j jours), avancement %, budget consomme
            projet(c1, "Refonte du site web", 18000d, 3, "Nouveau site vitrine responsive.", "TERMINE", 5, 150, -20, 100, 17000d);
            projet(c1, "Application mobile", 42000d, 6, "Application iOS / Android de suivi client.", "EN_COURS", 3, 90, 60, 45, 25000d);
            projet(c2, "Portail self-care", 65000d, 8, "Portail de gestion de compte pour les abonnes.", "EN_COURS", 2, 70, 25, 40, 45000d);
            projet(c2, "Migration cloud", 38000d, 5, "Migration de l'infrastructure vers le cloud.", "EN_ATTENTE", 1, -10, 160, 0, 0d);
            projet(c3, "Tableau de bord BI", 27000d, 4, "Reporting des ventes et de la production.", "TERMINE", 4, 120, -15, 100, 26000d);
            projet(c3, "Gestion des stocks", 22000d, 3, "Suivi des stocks en temps reel.", "EN_COURS", 0, 40, -5, 70, 18000d);
            log.info("Donnees de demonstration inserees");
        }
    }

    private Client client(String nom, String tel, String email, String adresse) {
        Client c = new Client();
        c.setNom(nom);
        c.setTel(tel);
        c.setEmail(email);
        c.setAdresse(adresse);
        return clientRepo.save(c);
    }

    private void projet(Client c, String libelle, double budget, int duree, String description,
                        String statut, int ilYaMois, int debutIlYaJours, int finDansJours,
                        int avancement, double budgetConsomme) {
        Projet p = new Projet();
        p.setLibelle(libelle);
        p.setBudget(budget);
        p.setDuree(duree);
        p.setDescription(description);
        p.setStatut(statut);
        p.setDateCreation(java.time.LocalDate.now().minusMonths(ilYaMois));
        p.setDateDebut(java.time.LocalDate.now().minusDays(debutIlYaJours));
        p.setDateFinPrevue(java.time.LocalDate.now().plusDays(finDansJours));
        p.setAvancement(avancement);
        p.setBudgetConsomme(budgetConsomme);
        p.setClient(c);
        projetRepo.save(p);
    }
}
