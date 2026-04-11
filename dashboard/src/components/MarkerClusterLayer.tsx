import { useEffect } from "react";
import { useMap } from "react-leaflet";
import L from "leaflet";
import "leaflet.markercluster";
import type { Visit } from "../types/api";
import { resultColor } from "../lib/format";

export function MarkerClusterLayer({ visits }: { visits: Visit[] }) {
  const map = useMap();

  useEffect(() => {
    const cluster = (L as unknown as { markerClusterGroup: () => any }).markerClusterGroup();

    visits.forEach((visit) => {
      if (visit.latitude == null || visit.longitude == null) return;
      const color = resultColor(visit.triageResult);
      const isRed = visit.triageResult === "RED";
      const icon = L.divIcon({
        html: `<div style="width:14px;height:14px;border-radius:999px;background:${color};border:2px solid white;box-shadow:${isRed ? "0 0 0 8px rgba(211,47,47,0.28)" : "0 0 0 4px rgba(0,0,0,0.15)"};${isRed ? "animation:pulseRed 1.2s infinite;" : ""}"></div>`,
        className: "",
        iconSize: [18, 18]
      });
      const marker = L.marker([visit.latitude, visit.longitude], { icon });
      marker.bindPopup(
        `<strong>${visit.worker?.name || visit.workerId}</strong><br/>${visit.patientType} - ${visit.triageResult}<br/>${new Date(
          visit.visitTimestamp
        ).toLocaleString()}`
      );
      cluster.addLayer(marker);
    });

    map.addLayer(cluster);
    return () => {
      map.removeLayer(cluster);
    };
  }, [map, visits]);

  return null;
}
