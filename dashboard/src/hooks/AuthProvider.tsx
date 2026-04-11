import { createContext, useContext, useMemo, useState } from "react";
import type { ReactNode } from "react";
import { api, clearAuthToken, setAuthToken } from "../api/client";
import type { LoginResponse } from "../types/api";

interface AuthContextValue {
  user: LoginResponse | null;
  login: (workerId: string, pin: string) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<LoginResponse | null>(null);

  const value = useMemo(
    () => ({
      user,
      login: async (workerId: string, pin: string) => {
        const { data } = await api.post<LoginResponse>("/auth/login", { workerId, pin });
        setAuthToken(data.token);
        setUser(data);
      },
      logout: () => {
        clearAuthToken();
        setUser(null);
      }
    }),
    [user]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) {
    throw new Error("useAuth must be used inside AuthProvider");
  }
  return ctx;
}
