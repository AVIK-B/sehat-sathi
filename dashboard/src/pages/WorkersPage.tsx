import { Link } from "react-router-dom";
import { useQuery } from "@tanstack/react-query";
import { api } from "../api/client";
import type { DashboardSummary } from "../types/api";
import { minutesAgo } from "../lib/format";

export function WorkersPage() {
  const { data, isLoading } = useQuery({
    queryKey: ["workers-summary"],
    queryFn: async () => {
      const res = await api.get<DashboardSummary>("/dashboard/summary");
      return res.data;
    }
  });

  if (isLoading || !data) return <div className="card">Loading...</div>;

  return (
    <div className="card overflow-x-auto">
      <h2 className="mb-4 text-xl font-black">All Workers</h2>
      <table className="w-full min-w-[700px] text-sm">
        <thead>
          <tr className="text-left text-slate-500">
            <th className="pb-2">Name</th>
            <th className="pb-2">Block</th>
            <th className="pb-2">Status</th>
            <th className="pb-2">Visits Today</th>
            <th className="pb-2">Last Synced</th>
            <th className="pb-2">Action</th>
          </tr>
        </thead>
        <tbody>
          {data.workers.map((w) => {
            const active = w.visitCountToday > 0;
            return (
              <tr key={w.id} className="border-t border-slate-100">
                <td className="py-3 font-semibold">{w.name}</td>
                <td className="py-3">{w.block}</td>
                <td className="py-3">
                  <span
                    className={`rounded-full px-3 py-1 text-xs font-bold text-white ${
                      active ? "bg-green-600" : "bg-slate-500"
                    }`}
                  >
                    {active ? "ACTIVE" : "IDLE"}
                  </span>
                </td>
                <td className="py-3">{w.visitCountToday}</td>
                <td className="py-3">{minutesAgo(w.lastSyncedAt)}</td>
                <td className="py-3">
                  <Link className="rounded-lg bg-brand-clay px-3 py-2 text-white" to={`/workers/${w.id}`}>
                    View
                  </Link>
                </td>
              </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  );
}
