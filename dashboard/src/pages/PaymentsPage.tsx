import { useQuery } from "@tanstack/react-query";
import { api } from "../api/client";
import type { DashboardSummary, WorkerStats } from "../types/api";

interface PaymentRow {
  workerId: string;
  name: string;
  amount: number;
}

export function PaymentsPage() {
  const { data, isLoading } = useQuery({
    queryKey: ["payments"],
    queryFn: async (): Promise<PaymentRow[]> => {
      const summary = await api.get<DashboardSummary>("/dashboard/summary");
      const workerRows = await Promise.all(
        summary.data.workers.map(async (w) => {
          const stats = await api.get<WorkerStats>(`/workers/${w.id}/stats`);
          return {
            workerId: w.id,
            name: w.name,
            amount: stats.data.incentiveTotal
          };
        })
      );
      return workerRows;
    }
  });

  if (isLoading || !data) return <div className="card">Loading...</div>;

  const total = data.reduce((sum, r) => sum + r.amount, 0);

  return (
    <div className="space-y-4">
      <div className="card">
        <h2 className="text-xl font-black">Incentive Payment Summary</h2>
        <p className="mt-1 text-sm text-slate-500">Total payable: INR {total}</p>
      </div>

      <div className="card overflow-x-auto">
        <table className="w-full min-w-[700px] text-sm">
          <thead>
            <tr className="text-left text-slate-500">
              <th className="pb-2">Worker</th>
              <th className="pb-2">Amount</th>
              <th className="pb-2">Action</th>
            </tr>
          </thead>
          <tbody>
            {data.map((row) => (
              <tr key={row.workerId} className="border-t border-slate-100">
                <td className="py-2">{row.name}</td>
                <td className="py-2">INR {row.amount}</td>
                <td className="py-2">
                  <button className="mr-2 rounded-lg bg-green-600 px-3 py-2 text-white">Approve</button>
                  <button className="rounded-lg bg-red-600 px-3 py-2 text-white">Dispute</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
