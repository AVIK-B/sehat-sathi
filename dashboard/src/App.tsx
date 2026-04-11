import { Link, Navigate, Outlet, Route, Routes, useLocation } from "react-router-dom";
import { useAuth } from "./hooks/AuthProvider";
import { ProtectedRoute } from "./hooks/useProtectedRoute";
import { LoginPage } from "./pages/LoginPage";
import { DashboardPage } from "./pages/DashboardPage";
import { WorkersPage } from "./pages/WorkersPage";
import { WorkerDetailPage } from "./pages/WorkerDetailPage";
import { MapPage } from "./pages/MapPage";
import { ReportsPage } from "./pages/ReportsPage";
import { PaymentsPage } from "./pages/PaymentsPage";

function Shell() {
  const { user, logout } = useAuth();
  const location = useLocation();

  const links = [
    ["/dashboard", "Dashboard"],
    ["/workers", "Workers"],
    ["/map", "Map"],
    ["/reports", "Reports"],
    ["/payments", "Payments"]
  ];

  return (
    <div className="min-h-screen bg-gradient-to-b from-brand-sand to-white text-brand-slate">
      <header className="border-b border-orange-200 bg-white/80 backdrop-blur">
        <div className="mx-auto flex max-w-7xl items-center justify-between px-4 py-3">
          <div>
            <h1 className="text-xl font-black tracking-tight">ASHA Supervisor</h1>
            <p className="text-sm text-slate-600">{user?.name}</p>
          </div>
          <button className="rounded-xl bg-brand-clay px-4 py-2 text-white" onClick={logout}>
            Logout
          </button>
        </div>
        <nav className="mx-auto flex max-w-7xl flex-wrap gap-2 px-4 pb-3">
          {links.map(([to, label]) => (
            <Link
              key={to}
              to={to}
              className={`rounded-full px-4 py-2 text-sm font-semibold ${
                location.pathname.startsWith(to)
                  ? "bg-brand-teal text-white"
                  : "bg-white text-brand-slate border border-slate-200"
              }`}
            >
              {label}
            </Link>
          ))}
        </nav>
      </header>
      <main className="mx-auto max-w-7xl p-4">
        <Outlet />
      </main>
    </div>
  );
}

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route
        element={
          <ProtectedRoute>
            <Shell />
          </ProtectedRoute>
        }
      >
        <Route path="/dashboard" element={<DashboardPage />} />
        <Route path="/workers" element={<WorkersPage />} />
        <Route path="/workers/:id" element={<WorkerDetailPage />} />
        <Route path="/map" element={<MapPage />} />
        <Route path="/reports" element={<ReportsPage />} />
        <Route path="/payments" element={<PaymentsPage />} />
      </Route>
      <Route path="*" element={<Navigate to="/dashboard" replace />} />
    </Routes>
  );
}
