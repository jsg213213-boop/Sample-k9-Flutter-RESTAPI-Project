/**
 * API Base URL
 *
 * - 웹 브라우저에서는 localhost / 호스트 사설 IP 사용 (10.0.2.2 금지)
 * - `.env.local` 의 NEXT_PUBLIC_API_BASE_URL 로 오버라이드
 */
export const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080/api";

/** `/generateToken` 은 `/api` 프리픽스 밖에 있음 (APILoginFilter) */
export const AUTH_BASE_URL = API_BASE_URL.replace(/\/api\/?$/, "");
