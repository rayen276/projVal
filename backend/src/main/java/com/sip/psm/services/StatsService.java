package com.sip.psm.services;

import com.sip.psm.dto.RisqueDto;
import com.sip.psm.dto.StatsDto;
import com.sip.psm.entities.Client;
import com.sip.psm.entities.Projet;
import com.sip.psm.repositories.ClientRepository;
import com.sip.psm.repositories.ProjetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class StatsService {

    private final ClientRepository clientRepo;
    private final ProjetRepository projetRepo;

    public StatsService(ClientRepository clientRepo, ProjetRepository projetRepo) {
        this.clientRepo = clientRepo;
        this.projetRepo = projetRepo;
    }

    public StatsDto compute() {
        List<Client> clients = clientRepo.findAll();
        List<Projet> projets = projetRepo.findAll();
        double total = projets.stream().mapToDouble(p -> p.getBudget() == null ? 0 : p.getBudget()).sum();
        double moyen = projets.isEmpty() ? 0 : total / projets.size();

        List<StatsDto.ClientStat> top = clients.stream()
                .map(c -> new StatsDto.ClientStat(c.getNom(), c.getProjets().size(),
                        c.getProjets().stream().mapToDouble(p -> p.getBudget() == null ? 0 : p.getBudget()).sum()))
                .sorted(Comparator.comparingDouble(StatsDto.ClientStat::budget).reversed())
                .limit(8)
                .toList();

        List<StatsDto.StatutStat> parStatut = new ArrayList<>();
        for (String st : List.of("EN_ATTENTE", "EN_COURS", "TERMINE")) {
            long nb = projets.stream().filter(p -> st.equals(statutOf(p))).count();
            parStatut.add(new StatsDto.StatutStat(st, nb));
        }

        // 6 derniers mois (mois courant inclus), meme les mois sans projet
        List<StatsDto.MoisStat> parMois = new ArrayList<>();
        YearMonth now = YearMonth.now();
        for (int i = 5; i >= 0; i--) {
            YearMonth ym = now.minusMonths(i);
            List<Projet> du = projets.stream().filter(p -> {
                LocalDate d = p.getDateCreation();
                return d != null && YearMonth.from(d).equals(ym);
            }).toList();
            double b = du.stream().mapToDouble(p -> p.getBudget() == null ? 0 : p.getBudget()).sum();
            parMois.add(new StatsDto.MoisStat(ym.toString(), du.size(), b));
        }

        // Risque : calcule pour chaque projet non termine
        LocalDate today = LocalDate.now();
        List<StatsDto.RisqueStat> parRisque = new ArrayList<>();
        List<StatsDto.ProjetRisque> aRisque = new ArrayList<>();
        java.util.Map<String, Long> compte = new java.util.LinkedHashMap<>();
        for (String n : List.of("FAIBLE", "MOYEN", "ELEVE", "CRITIQUE", "NON_EVALUE")) compte.put(n, 0L);
        for (Projet p : projets) {
            if ("TERMINE".equals(statutOf(p))) continue;
            RisqueDto r = RisqueCalculator.calculer(p, today);
            compte.merge(r.niveau(), 1L, Long::sum);
            if ("ELEVE".equals(r.niveau()) || "CRITIQUE".equals(r.niveau())) {
                aRisque.add(new StatsDto.ProjetRisque(p.getId(), p.getLibelle(), p.getClient().getNom(), r.niveau(),
                        r.score(), r.joursRestants(), p.getAvancement() == null ? 0 : p.getAvancement(), r.message()));
            }
        }
        compte.forEach((n, v) -> parRisque.add(new StatsDto.RisqueStat(n, v)));
        aRisque.sort(Comparator.comparingInt(StatsDto.ProjetRisque::score).reversed());

        return new StatsDto(clients.size(), projets.size(), total, moyen, top, parStatut, parMois,
                parRisque, aRisque.stream().limit(5).toList());
    }

    private static String statutOf(Projet p) {
        return p.getStatut() == null ? "EN_COURS" : p.getStatut();
    }
}
