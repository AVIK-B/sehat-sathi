import { useQuery } from "@tanstack/react-query";
import { useParams } from "react-router-dom";
import { MapContainer, TileLayer } from "react-leaflet";
import { api } from "../api/client";
import type { Visit, WorkerStats } from "../types/api";
import { MarkerClusterLayer } from "../components/MarkerClusterLayer";
import { ResultBadge } from "../components/ResultBadge";

export function WorkerDetailPage() {
  const { id = "" } = useParams();

  const stats = useQuery({
    queryKey: ["worker-stats", id],
    queryFn: async () => {
      const res = await api.get<WorkerStats>(`/workers/${id}/stats`);
      return res.data;
    }
  });

  const visits = useQuery({
    queryKey: ["worker-visits", id],
    queryFn: async () => {
      const res = await api.get<Visit[]>(`/visits?workerId=${id}`);
      return res.data;
    }
  });

  if (stats.isLoading || visits.isLoading || !stats.data || !visits.data) {
    return <div className="card">Loading...</div>;
  }

  return (
    <div className="space-y-4">
      <div className="grid gap-4 md:grid-cols-3">
        <div className="card">
          <p className="text-sm text-slate-500">Visit Count</p>
          <p className="metric-value">{stats.data.visitCount}</p>
        </div>
        <div className="card">
          <p className="text-sm text-slate-500">Referral Rate</p>
          <p className="metric-value">{Math.round(stats.data.referralRate * 100)}%</p>
        </div>
        <div className="card">
          <p className="text-sm text-slate-500">Earnings</p>
          <p className="metric-value">INR {stats.data.incentiveTotal}</p>
        </div>
      </div>

      <div className="grid gap-4 lg:grid-cols-2">
        <div className="card h-[480px]">
          <h2 className="mb-2 text-lg font-bold">Visit Map</h2>
          <MapContainer center={[26.85, 80.94]} zoom={10} style={{ height: "420px" }}>
            <TileLayer url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png" />
            <MarkerClusterLayer visits={visits.data} />
          </MapContainer>
        </div>
        <div className="card overflow-auto">
          <h2 className="mb-2 text-lg font-bold">Visit History</h2>
          <div className="space-y-2">
            {visits.data.map((v) => (
              <div key={v.id} className="rounded-xl border border-slate-200 p-3">
                <div className="flex items-center justify-between">
                  <p className="font-semibold">{new Date(v.visitTimestamp).toLocaleString()}</p>
                  <ResultBadge result={v.triageResult} />
                </div>
                <p className="text-sm text-slate-600">
                  {v.patientType} - {v.chiefComplaint}
                </p>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}
