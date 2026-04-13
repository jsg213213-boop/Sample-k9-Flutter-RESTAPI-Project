/**
 * JWT 토큰/회원정보 저장 유틸
 * - 초기 구현은 localStorage (XSS 주의, 향후 httpOnly 쿠키 마이그레이션 권장)
 * - 클라이언트 전용
 */

const TOKEN_KEY = "accessToken";
const REFRESH_KEY = "refreshToken";
const MEMBER_KEY = "memberInfo";

export interface MemberInfo {
  id: number;
  mid: string;
  mname: string;
  email?: string;
  region?: string;
  role?: string;
  profileImg?: string;
  regDate?: string;
}

export function saveToken(token: string, refresh?: string): void {
  if (typeof window === "undefined") return;
  localStorage.setItem(TOKEN_KEY, token);
  if (refresh) localStorage.setItem(REFRESH_KEY, refresh);
}

export function loadToken(): string | null {
  if (typeof window === "undefined") return null;
  return localStorage.getItem(TOKEN_KEY);
}

export function loadRefreshToken(): string | null {
  if (typeof window === "undefined") return null;
  return localStorage.getItem(REFRESH_KEY);
}

export function clearToken(): void {
  if (typeof window === "undefined") return;
  localStorage.removeItem(TOKEN_KEY);
  localStorage.removeItem(REFRESH_KEY);
  localStorage.removeItem(MEMBER_KEY);
}

export function saveMember(member: MemberInfo): void {
  if (typeof window === "undefined") return;
  localStorage.setItem(MEMBER_KEY, JSON.stringify(member));
}

export function loadMember(): MemberInfo | null {
  if (typeof window === "undefined") return null;
  const raw = localStorage.getItem(MEMBER_KEY);
  if (!raw) return null;
  try {
    return JSON.parse(raw) as MemberInfo;
  } catch {
    return null;
  }
}

/**
 * JWT exp(초 단위 epoch) 파싱 → 남은 ms 반환. 파싱 실패 시 null.
 */
export function getTokenRemainingMs(token: string): number | null {
  try {
    const payload = token.split(".")[1];
    if (!payload) return null;
    const decoded = JSON.parse(
      atob(payload.replace(/-/g, "+").replace(/_/g, "/")),
    );
    if (typeof decoded.exp !== "number") return null;
    return decoded.exp * 1000 - Date.now();
  } catch {
    return null;
  }
}
