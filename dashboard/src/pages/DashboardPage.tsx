import { useQuery } from "@tanstack/react-query";
import { ResponsiveContainer, PieChart, Pie, Cell, Tooltip } from "recharts";
import { api } from "../api/client";
import type { DashboardSummary } from "../types/api";
import { StatCard } from "../components/StatCard";

const colors: Record<string, string> = {
  RED: "#D32F2F",
  YELLOW: "#F9A825",
  GREEN: "#388E3C"
};

export function DashboardPage() {
  const { data, isLoading } = useQuery({
    queryKey: ["dashboard-summary"],
    queryFn: async () => {
      const res = await api.get<DashboardSummary>("/dashboard/summary");
      return res.data;
    }
  });

  if (isLoading || !data) return <div className="card">Loading...</div>;

  const chartData = data.byResult.map((x) => ({ name: x.triageResult, value: x._count._all }));

  return (
    <div className="space-y-4">
      <div className="grid gap-4 md:grid-cols-3">
        <StatCard title="Total visits today" value={String(data.totalVisitsToday)} />
        <StatCard title="Referral rate" value={`${Math.round(data.referralRate * 100)}%`} />
        <StatCard title="Active workers" value={String(data.workers.filter((w) => w.visitCountToday > 0).length)} />
      </div>

      <div className="grid gap-4 lg:grid-cols-2">
        <div className="card h-80">
          <h2 className="text-lg font-bold">Result distribution</h2>
          <ResponsiveContainer width="100%" height="90%">
            <PieChart>
              <Pie data={chartData} dataKey="value" nameKey="name" innerRadius={45} outerRadius={90}>
                {chartData.map((entry) => (
                  <Cell key={entry.name} fill={colors[entry.name]} />
                ))}
              </Pie>
              <Tooltip />
            </PieChart>
          </ResponsiveContainer>
        </div>

        <div className="card">
          <h2 className="text-lg font-bold">Worker snapshots</h2>
          <div className="mt-3 space-y-2">
            {data.workers.slice(0, 8).map((w) => (
              <div key={w.id} className="flex items-center justify-between rounded-xl bg-slate-50 p-3">
                <div>
                  <p className="font-semibold">{w.name}</p>
                  <p className="text-xs text-slate-500">
                    {w.block}, {w.district}
                  </p>
                </div>
                <span className="rounded-full bg-brand-teal px-3 py-1 text-xs font-bold text-white">
                  {w.visitCountToday} visits
                </span>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}
