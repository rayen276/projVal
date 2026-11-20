export const ROLE_ADMIN = 'ADMIN';
export const ROLE_COLLABORATEUR = 'COLLABORATEUR';

export interface UserDto {
  id: number;
  email: string;
  nom: string;
  prenom: string;
  tel?: string;
  roles: string[];
}

export interface AuthResponse {
  token: string;
  user: UserDto;
}

export interface RegisterPayload {
  email: string;
  password: string;
  nom: string;
  prenom: string;
  tel?: string;
}

export interface UserUpdatePayload {
  nom: string;
  prenom: string;
  tel?: string;
  roles: string[];
}

export interface Client {
  id?: number;
  nom: string;
  tel?: string;
  email?: string;
  adresse?: string;
  nbProjets?: number;
}

export interface Projet {
  id?: number;
  libelle: string;
  budget: number;
  duree: number;
  description?: string;
  statut?: string;
  dateCreation?: string;
  dateDebut?: string;
  dateFinPrevue?: string;
  avancement?: number;
  budgetConsomme?: number | null;
  risque?: Risque;
  clientId: number;
  clientNom?: string;
}

export interface Risque {
  niveau: string;            // FAIBLE | MOYEN | ELEVE | CRITIQUE | NON_EVALUE
  score: number;             // 0 - 100
  joursRestants: number;     // negatif = en retard
  enRetard: boolean;
  pctTemps: number;          // % du temps planifie deja ecoule
  dateFinEstimee?: string;   // au rythme actuel
  retardEstimeJours?: number;
  message: string;
}

export interface ProjetRisque {
  id: number;
  libelle: string;
  clientNom: string;
  niveau: string;
  score: number;
  joursRestants: number;
  avancement: number;
  message: string;
}

export const RISQUES: { value: string; label: string; badge: string; color: string }[] = [
  { value: 'FAIBLE', label: 'Faible', badge: 'bg-success', color: '#0ab39c' },
  { value: 'MOYEN', label: 'Moyen', badge: 'bg-warning', color: '#f7b84b' },
  { value: 'ELEVE', label: 'Eleve', badge: 'bg-danger', color: '#f06548' },
  { value: 'CRITIQUE', label: 'Critique', badge: 'bg-dark', color: '#212529' },
  { value: 'NON_EVALUE', label: 'Non evalue', badge: 'bg-secondary', color: '#878a99' }
];

export function risqueInfo(niveau?: string) {
  return RISQUES.find(r => r.value === niveau) ?? RISQUES[4];
}

export interface ClientStat {
  clientNom: string;
  nbProjets: number;
  budget: number;
}

export interface Stats {
  totalClients: number;
  totalProjets: number;
  budgetTotal: number;
  budgetMoyen: number;
  topClients: ClientStat[];
  parStatut: { statut: string; nb: number }[];
  parMois: { mois: string; nb: number; budget: number }[];
  parRisque: { niveau: string; nb: number }[];
  projetsARisque: ProjetRisque[];
}

export const STATUTS: { value: string; label: string; color: string; badge: string }[] = [
  { value: 'EN_ATTENTE', label: 'En attente', color: '#f7b84b', badge: 'bg-warning' },
  { value: 'EN_COURS', label: 'En cours', color: '#299cdb', badge: 'bg-info' },
  { value: 'TERMINE', label: 'Termine', color: '#0ab39c', badge: 'bg-success' }
];

export function statutLabel(v?: string): string {
  return STATUTS.find(s => s.value === (v ?? 'EN_COURS'))?.label ?? (v ?? '');
}
