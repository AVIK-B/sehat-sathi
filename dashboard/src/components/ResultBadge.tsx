export function ResultBadge({ result }: { result: string }) {
  const cls =
    result === "RED"
      ? "bg-red-600"
      : result === "YELLOW"
        ? "bg-yellow-500"
        : "bg-green-600";
  return <span className={`rounded-full px-3 py-1 text-xs font-bold text-white ${cls}`}>{result}</span>;
}
