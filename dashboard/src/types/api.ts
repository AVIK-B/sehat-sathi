export type TriageResult = "RED" | "YELLOW" | "GREEN";

export interface LoginResponse {
  token: string;
  workerId: string;
  name: string;
  block: string;
  district: string;
  role: "WORKER" | "SUPERVISOR";
}

export interface Visit {
  id: string;
  workerId: string;
  patientType: string;
  patientAgeMonths: number | null;
  chiefComplaint: string;
  triageResult: TriageResult;
  adviceGiven: unknown;
  referralRequired: boolean;
  referralUrgency: string;
  latitude: number | null;
  longitude: number | null;
  visitTimestamp: string;
  synced: boolean;
  syncAttempts: number;
  serverReceivedAt: string;
  worker?: {
    id: string;
    name: string;
    block: string;
    district: string;
  };
}

export interface WorkerSummary {
  id: string;
  name: string;
  block: string;
  district: string;
  visitCountToday: number;
  lastSyncedAt: string | null;
}

export interface DashboardSummary {
  totalVisitsToday: number;
  totalReferralsToday: number;
  referralRate: number;
  byResult: Array<{ triageResult: TriageResult; _count: { _all: number } }>;
  workers: WorkerSummary[];
}

export interface WorkerStats {
  workerId: string;
  visitCount: number;
  referralRate: number;
  incentiveTotal: number;
}
