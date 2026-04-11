export function StatCard({
  title,
  value,
  subtitle
}: {
  title: string;
  value: string;
  subtitle?: string;
}) {
  return (
    <div className="card">
      <p className="text-sm uppercase tracking-wide text-slate-500">{title}</p>
      <p className="metric-value mt-2">{value}</p>
      {subtitle ? <p className="mt-2 text-sm text-slate-500">{subtitle}</p> : null}
    </div>
  );
}
