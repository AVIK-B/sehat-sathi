import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { api } from "../api/client";
import type { Visit } from "../types/api";

function toCsv(visits: Visit[]): string {
  const header = ["worker_name", "date", "patient_type", "result", "latitude", "longitude"];
  const rows = visits.map((v) => [
    v.worker?.name || v.workerId,
    new Date(v.visitTimestamp).toISOString(),
    v.patientType,
    v.triageResult,
    String(v.latitude ?? ""),
    String(v.longitude ?? "")
  ]);
  return [header, ...rows].map((r) => r.join(",")).join("\n");
}

export function ReportsPage() {
  const [from, setFrom] = useState(new Date(Date.now() - 7 * 86400000).toISOString().slice(0, 10));
  const [to, setTo] = useState(new Date().toISOString().slice(0, 10));

  const { data, isLoading } = useQuery({
    queryKey: ["reports", from, to],
    queryFn: async () => {
      const res = await api.get<Visit[]>(`/visits?from=${from}&to=${to}`);
      return res.data;
    }
  });

  return (
    <div className="space-y-4">
      <div className="card flex flex-wrap items-end gap-3">
        <div>
          <label className="text-sm font-semibold">From</label>
          <input className="mt-1 rounded-lg border p-2" type="date" value={from} onChange={(e) => setFrom(e.target.value)} />
        </div>
        <div>
          <label className="text-sm font-semibold">To</label>
          <input className="mt-1 rounded-lg border p-2" type="date" value={to} onChange={(e) => setTo(e.target.value)} />
        </div>
        <button
          className="rounded-xl bg-brand-teal px-4 py-2 font-semibold text-white"
          onClick={() => {
            if (!data) return;
            const blob = new Blob([toCsv(data)], { type: "text/csv;charset=utf-8;" });
            const link = document.createElement("a");
            link.href = URL.createObjectURL(blob);
            link.download = `visit-report-${from}-to-${to}.csv`;
            link.click();
          }}
        >
          Export CSV
        </button>
      </div>

      <div className="card overflow-x-auto">
        {isLoading ? (
          <p>Loading...</p>
        ) : (
          <table className="w-full min-w-[700px] text-sm">
            <thead>
              <tr className="text-left text-slate-500">
                <th className="pb-2">Worker</th>
                <th className="pb-2">Date</th>
                <th className="pb-2">Patient Type</th>
                <th className="pb-2">Result</th>
                <th className="pb-2">GPS</th>
              </tr>
            </thead>
            <tbody>
              {(data || []).map((v) => (
                <tr key={v.id} className="border-t border-slate-100">
                  <td className="py-2">{v.worker?.name || v.workerId}</td>
                  <td className="py-2">{new Date(v.visitTimestamp).toLocaleString()}</td>
                  <td className="py-2">{v.patientType}</td>
                  <td className="py-2">{v.triageResult}</td>
                  <td className="py-2">
                    {v.latitude}, {v.longitude}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
}
