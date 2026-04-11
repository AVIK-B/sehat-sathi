export function minutesAgo(iso: string | null): string {
  if (!iso) return "never";
  const diff = Date.now() - new Date(iso).getTime();
  const mins = Math.max(0, Math.floor(diff / 60000));
  if (mins < 1) return "just now";
  if (mins < 60) return `${mins} min ago`;
  const hours = Math.floor(mins / 60);
  return `${hours} hr ago`;
}

export function resultColor(result: string): string {
  if (result === "RED") return "#D32F2F";
  if (result === "YELLOW") return "#F9A825";
  return "#388E3C";
}
