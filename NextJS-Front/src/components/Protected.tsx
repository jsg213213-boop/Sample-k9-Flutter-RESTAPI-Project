"use client";

import { useEffect, ReactNode } from "react";
import { useRouter } from "next/navigation";
import { useAuth } from "@/lib/auth-context";

interface Props {
  children: ReactNode;
  /** "ADMIN" 지정 시 관리자 권한 필요 */
  requireRole?: "ADMIN";
}

/**
 * 인증 가드. 미로그인 → /login 리다이렉트.
 * requireRole="ADMIN" 이면 권한 미달 시 홈으로 리다이렉트.
 */
export default function Protected({ children, requireRole }: Props) {
  const { member, loading } = useAuth();
  const router = useRouter();
  const isAdmin = member?.role === "ADMIN" || member?.role === "ROLE_ADMIN";

  useEffect(() => {
    if (loading) return;
    if (!member) {
      router.replace("/login");
      return;
    }
    if (requireRole === "ADMIN" && !isAdmin) {
      router.replace("/");
    }
  }, [loading, member, isAdmin, requireRole, router]);

  if (loading || !member || (requireRole === "ADMIN" && !isAdmin)) {
    return (
      <main className="flex min-h-screen items-center justify-center">
        <p className="text-gray-500">확인 중...</p>
      </main>
    );
  }

  return <>{children}</>;
}
