import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../hooks/AuthProvider";

export function LoginPage() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [workerId, setWorkerId] = useState("sup001");
  const [pin, setPin] = useState("1234");
  const [error, setError] = useState("");

  return (
    <div className="flex min-h-screen items-center justify-center bg-gradient-to-br from-brand-sand to-orange-100 p-4">
      <div className="w-full max-w-md rounded-3xl bg-white p-6 shadow-card">
        <h1 className="text-3xl font-black text-brand-slate">Supervisor Login</h1>
        <p className="mt-2 text-sm text-slate-600">Use your supervisor worker ID and 4 digit PIN.</p>

        <div className="mt-6 space-y-4">
          <label className="block text-sm font-semibold">Worker ID</label>
          <input
            className="w-full rounded-xl border border-slate-300 p-3"
            value={workerId}
            onChange={(e) => setWorkerId(e.target.value)}
          />

          <label className="block text-sm font-semibold">PIN</label>
          <input
            className="w-full rounded-xl border border-slate-300 p-3"
            value={pin}
            type="password"
            onChange={(e) => setPin(e.target.value)}
          />

          {error ? <p className="text-sm text-red-600">{error}</p> : null}

          <button
            className="w-full rounded-xl bg-brand-teal py-3 text-lg font-bold text-white"
            onClick={async () => {
              try {
                await login(workerId, pin);
                navigate("/dashboard");
              } catch {
                setError("Login failed");
              }
            }}
          >
            Login
          </button>
        </div>
      </div>
    </div>
  );
}
