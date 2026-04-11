import { useQuery } from "@tanstack/react-query";
import { MapContainer, TileLayer } from "react-leaflet";
import { api } from "../api/client";
import type { Visit } from "../types/api";
import { MarkerClusterLayer } from "../components/MarkerClusterLayer";

export function MapPage() {
  const today = new Date();
  const start = new Date(today.getFullYear(), today.getMonth(), today.getDate()).toISOString();
  const end = new Date(today.getFullYear(), today.getMonth(), today.getDate(), 23, 59, 59).toISOString();

  const { data, isLoading } = useQuery({
    queryKey: ["map-visits", start, end],
    queryFn: async () => {
      const res = await api.get<Visit[]>(`/visits?from=${encodeURIComponent(start)}&to=${encodeURIComponent(end)}`);
      return res.data;
    }
  });

  if (isLoading || !data) return <div className="card">Loading...</div>;

  return (
    <div className="card h-[78vh]">
      <h2 className="mb-2 text-xl font-black">Today Visit Map</h2>
      <MapContainer center={[26.85, 80.94]} zoom={8} style={{ height: "calc(78vh - 60px)" }}>
        <TileLayer url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png" />
        <MarkerClusterLayer visits={data} />
      </MapContainer>
    </div>
  );
}
