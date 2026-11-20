import { Component, OnInit } from '@angular/core';
import { forkJoin } from 'rxjs';
import { Projet, RISQUES, STATUTS, Stats, risqueInfo } from '../../core/models/psm.models';
import { ProjetService } from '../../core/services/projet.service';
import { SessionService } from '../../core/services/session.service';
import { StatsService } from '../../core/services/stats.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html'
})
export class DashboardComponent implements OnInit {

  stats?: Stats;
  derniersProjets: Projet[] = [];
  loading = true;

  // Options ApexCharts (ng-apexcharts) construites apres reception des stats
  risqueInfo = risqueInfo;
  risqueChart: any;
  clientChart: any;
  statutChart: any;
  moisChart: any;
  error = '';

  constructor(private statsService: StatsService, private projetService: ProjetService,
              public session: SessionService) { }

  ngOnInit(): void {
    forkJoin({ stats: this.statsService.get(), projets: this.projetService.getAll() }).subscribe({
      next: ({ stats, projets }) => {
        this.stats = stats;
        this.buildCharts(stats);
        this.derniersProjets = projets.slice(0, 5);   // l'API renvoie du plus recent au plus ancien
        this.loading = false;
      },
      error: err => { this.error = err.message; this.loading = false; }
    });
  }

  private buildCharts(s: Stats): void {
    // 0) Projets en cours par niveau de risque : donut
    const rs = s.parRisque.filter(r => r.nb > 0);
    this.risqueChart = {
      series: rs.map(r => r.nb),
      labels: rs.map(r => RISQUES.find(x => x.value === r.niveau)?.label ?? r.niveau),
      colors: rs.map(r => RISQUES.find(x => x.value === r.niveau)?.color ?? '#999'),
      chart: { type: 'donut', height: 260 },
      legend: { position: 'bottom' },
      dataLabels: { enabled: true }
    };

    // 1) Budget par client : barres horizontales
    this.clientChart = {
      series: [{ name: 'Budget (DT)', data: s.topClients.map(c => c.budget) }],
      chart: { type: 'bar', height: 300, toolbar: { show: false } },
      plotOptions: { bar: { horizontal: true, borderRadius: 4, barHeight: '60%' } },
      xaxis: { categories: s.topClients.map(c => c.clientNom) },
      colors: ['#405189'],
      dataLabels: { enabled: false },
      tooltip: { y: { formatter: (v: number) => v.toLocaleString('fr-FR') + ' DT' } }
    };

    // 2) Projets par statut : donut
    this.statutChart = {
      series: s.parStatut.map(x => x.nb),
      labels: s.parStatut.map(x => STATUTS.find(st => st.value === x.statut)?.label ?? x.statut),
      colors: s.parStatut.map(x => STATUTS.find(st => st.value === x.statut)?.color ?? '#999'),
      chart: { type: 'donut', height: 300 },
      legend: { position: 'bottom' },
      dataLabels: { enabled: true }
    };

    // 3) Evolution mensuelle : budget (colonnes) + nombre de projets (ligne)
    const mois = ['Jan', 'Fev', 'Mar', 'Avr', 'Mai', 'Juin', 'Juil', 'Aout', 'Sep', 'Oct', 'Nov', 'Dec'];
    this.moisChart = {
      series: [
        { name: 'Budget (DT)', type: 'column', data: s.parMois.map(m => m.budget) },
        { name: 'Projets', type: 'line', data: s.parMois.map(m => m.nb) }
      ],
      chart: { type: 'line', height: 300, toolbar: { show: false } },
      stroke: { width: [0, 3], curve: 'smooth' },
      colors: ['#405189', '#0ab39c'],
      dataLabels: { enabled: false },
      xaxis: { categories: s.parMois.map(m => mois[Number(m.mois.slice(5, 7)) - 1] + ' ' + m.mois.slice(2, 4)) },
      yaxis: [
        { title: { text: 'Budget (DT)' }, labels: { formatter: (v: number) => Math.round(v).toLocaleString('fr-FR') } },
        { opposite: true, title: { text: 'Projets' }, min: 0, forceNiceScale: true,
          labels: { formatter: (v: number) => String(Math.round(v)) } }
      ],
      legend: { position: 'top' }
    };
  }

  /** Part du budget d'un client dans le budget total (barre de progression). */
  part(budget: number): number {
    const total = this.stats?.budgetTotal ?? 0;
    return total > 0 ? Math.round((budget / total) * 100) : 0;
  }
}
