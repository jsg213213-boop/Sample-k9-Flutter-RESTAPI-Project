"use client";

import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useRef,
  useState,
  ReactNode,
} from "react";
import {
  clearToken,
  getTokenRemainingMs,
  loadMember,
  loadToken,
  MemberInfo,
  saveMember,
  saveToken,
} from "./auth";

interface AuthContextValue {
  token: string | null;
  member: MemberInfo | null;
  loading: boolean;
  login: (token: string, member: MemberInfo, refresh?: string) => void;
  logout: () => void;
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [token, setToken] = useState<string | null>(null);
  const [member, setMember] = useState<MemberInfo | null>(null);
  const [loading, setLoading] = useState(true);
  const timerRef = useRef<ReturnType<typeof setTimeout> | null>(null);

  const logout = useCallback(() => {
    if (timerRef.current) clearTimeout(timerRef.current);
    clearToken();
    setToken(null);
    setMember(null);
  }, []);

  const scheduleAutoLogout = useCallback(
    (t: string) => {
      if (timerRef.current) clearTimeout(timerRef.current);
      const remaining = getTokenRemainingMs(t);
      if (remaining === null) return;
      if (remaining <= 0) {
        logout();
        return;
      }
      timerRef.current = setTimeout(() => {
        logout();
        if (typeof window !== "undefined") window.location.href = "/login";
      }, remaining);
    },
    [logout],
  );

  useEffect(() => {
    const t = loadToken();
    const m = loadMember();
    setToken(t);
    setMember(m);
    if (t) scheduleAutoLogout(t);
    setLoading(false);
    return () => {
      if (timerRef.current) clearTimeout(timerRef.current);
    };
  }, [scheduleAutoLogout]);

  const login = (newToken: string, newMember: MemberInfo, refresh?: string) => {
    saveToken(newToken, refresh);
    saveMember(newMember);
    setToken(newToken);
    setMember(newMember);
    scheduleAutoLogout(newToken);
  };

  return (
    <AuthContext.Provider value={{ token, member, loading, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth(): AuthContextValue {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used within <AuthProvider>");
  return ctx;
}
