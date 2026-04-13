# 작업 현황 & 체크리스트 (PROGRESS)

> **📌 작업 전 반드시 이 문서를 먼저 읽고, 작업 후 상태를 갱신한 뒤 커밋하세요.**
>
> 프로젝트 전체(Flutter-Front / Spring-Back / NextJS-Front) 의 진행 상황을
> 한 곳에서 추적하는 **단일 출처(Single Source of Truth)** 문서입니다.

---

## 0. 문서 사용 규칙

1. **세션 시작 시**: 이 파일을 먼저 Read 하여 현재 상황 파악.
2. **작업 중**: "다음 작업 후보" 중 우선순위 높은 항목부터 진행.
3. **작업 완료 시**:
   - 해당 체크박스를 `[x]` 로 전환.
   - "작업 이력" 섹션 상단에 날짜 + 요약 + 커밋 해시 추가.
   - "다음 작업 후보" 갱신 (완료 항목 제거, 새 항목 추가).
4. **커밋 포함**: 본 문서 갱신은 관련 작업 커밋에 함께 포함 (별도 커밋 금지).
5. **범위 질문**: 할 일이 모호하면 "다음 작업 후보" 항목을 기준으로 사용자에게 확인.

---

## 1. 프로젝트 개요

| 영역 | 위치 | 상태 |
|---|---|---|
| 백엔드 (Spring Boot 3 + JPA + MariaDB) | [Spring-Back/SpringBasic/api5012/](Spring-Back/SpringBasic/api5012/) | ✅ 운영 중 |
| 모바일 프론트 (Flutter) | [Flutter-Front/](Flutter-Front/) | ✅ 주요 기능 구현 완료 |
| 웹 프론트 (Next.js 15) | [NextJS-Front/](NextJS-Front/) | 🚧 Phase 0 스캐폴딩 완료, Phase 1 진행 예정 |
| Flask 예측 서비스 | [Flask-Back/](Flask-Back/) | 🔧 보조 (선택) |

### 핵심 연관 문서
- [SETUP.md](SETUP.md) — 프로젝트 클론/포크, 백엔드·Flutter 실행, 에뮬레이터/실기기 IP 주의사항
- [README.md](README.md) — 프로젝트 소개
- [API_DOCS.md](API_DOCS.md) — API 명세
- [FEATURE_FILES.md](FEATURE_FILES.md) — 기능별 파일 매핑
- [docs/ADMIN_FEATURE_FLOW.md](docs/ADMIN_FEATURE_FLOW.md) — 관리자 기능 흐름
- [NextJS-Front/PLAN.md](NextJS-Front/PLAN.md) — Next.js 포팅 기획서 및 Phase 체크리스트
- [NextJS-Front/README.md](NextJS-Front/README.md) — Next.js 실행 가이드

---

## 2. 현재 작업 중

*(현재 활성 작업 없음 — Phase 1 완료, Phase 2~3 주요 화면 초기 구현 완료. 다음: 로컬 `npm install && npm run dev` 검증 후 실제 API 흐름 테스트)*

---

## 3. 다음 작업 후보 (우선순위 순)

### 🔴 P0 — 즉시 진행 가능 / 기반 검증
- [x] **Next.js 로컬 검증**: `npm install` + `npx tsc --noEmit` + `npx vitest run` + `npx next build` 모두 통과 (21 routes, 0 type errors, 9/9 tests)
- [x] **로그인 API 스키마 정합성 확인**: 실제 엔드포인트는 `POST /generateToken` (APILoginFilter, `/api` 프리픽스 밖). `constants/api.ts` 에 `AUTH_BASE_URL` 추가, [login/page.tsx](NextJS-Front/src/app/login/page.tsx) 가 `/generateToken` → `/api/member/me` 2단계 호출로 수정됨
- [ ] **CORS 설정 점검**: [CustomSecurityConfig.java](Spring-Back/SpringBasic/api5012/src/main/java/com/busanit501/api5012/config/CustomSecurityConfig.java) 가 `http://localhost:3000` Origin 을 허용하는지 확인 (백엔드 띄워 실제 API 호출 테스트 필요)

### 🟠 P1 — Next.js Phase 1 (인증 & 홈)
- [x] `/signup` 페이지 — 회원가입 폼 (MemberSignupDTO + mid/email 중복확인 + base64 프로필)
- [x] `Navbar.tsx` 공통 컴포넌트 (로그인/로그아웃 토글, 관리자 배지, 루트 레이아웃 반영)
- [x] `Protected.tsx` 인증 가드 컴포넌트 (role="ADMIN" 지원)
- [x] JWT 만료 시간 파싱 → 자동 로그아웃 타이머 (`getTokenRemainingMs` + AuthProvider 스케줄러)

### 🟡 P2 — Next.js Phase 2 (도서 / 마이페이지)
- [x] `/books` 검색 바 + 페이지네이션 (`GET /api/book?keyword=&page=&size=`)
- [x] `/books/[id]` 도서 상세 (coverImage, 대여 신청 버튼)
- [x] 대여 신청 / 반납 / 연장 API 호출 플로우 (`POST /api/rental`, `PUT /{id}/return`, `PUT /{id}/extend`)
- [x] `/mypage` 내 정보
- [x] `/mypage/rentals` 대여 이력 (`GET /api/rental?memberId=&page=&size=`)
- [x] `/mypage/edit` 내 정보 수정 + 프로필 이미지 업로드 (base64 → `PUT /api/member/profile-image`)

### 🟢 P3 — Next.js Phase 3 (공지/문의/행사)
- [x] `/notices` 공지 목록 + 상단고정 스타일
- [x] `/notices/[id]` 상세 + 이미지 표시
- [x] `/inquiries` 목록 (비밀글 마스킹, 본인/관리자만 열람)
- [x] `/inquiries/[id]` 상세 + 답변 표시
- [x] `/inquiries/new` 작성 (secret 토글)
- [x] `/events` 목록 + 신청 (`POST /api/event/{id}/apply`)

### 🔵 P4 — Next.js Phase 4 (관리자)
- [x] `/admin` 대시보드 (6개 서브메뉴, ADMIN 롤 가드)
- [x] `/admin/books` 도서 등록/삭제 (수정은 향후 상세 편집 페이지 분리)
- [x] `/admin/notices` 공지 등록/삭제 (상단고정 토글) — 이미지 업로드는 향후 multipart 지원 추가
- [x] `/admin/inquiries` 답변 등록/삭제
- [x] `/admin/events` 행사 등록/삭제
- [x] `/admin/members` 회원 목록/삭제
- [x] `/admin/facility` 시설예약 목록/승인/반려/삭제

### ⚪ P5 — 부가/선택
- [ ] `/ai` AI 예측 (Flask 연동)
- [ ] 다크모드
- [x] **단위 테스트 인프라**: Vitest + jsdom + `@/` 에일리어스 설정, `auth.ts` 9 케이스 통과 — `npm test` / `npm run test:watch`
- [ ] 도서/공지/문의 상세 편집 페이지 (현재는 등록/삭제만)
- [ ] 관리자 공지 이미지 업로드 (multipart/form-data)
- [ ] E2E 테스트 (Playwright)

### 🔧 기술 부채 / 개선
- [x] Spring `BookDTO` 실제 필드와 [NextJS-Front/src/types/book.ts](NextJS-Front/src/types/book.ts) 재대조 (`bookImage` → `coverImage`, 미존재 필드 제거)
- [ ] JWT 저장소를 localStorage → httpOnly 쿠키로 마이그레이션 (XSS 방어)
- [ ] Next.js 페이지별 Suspense / loading.tsx 추가
- [ ] Flutter 앱의 상수·API 주소를 dotenv 로 분리
- [ ] 공지 이미지 서빙 경로 (`/api/notice/image/{uuid}_{fileName}`) 실제 백엔드 라우트와 정합성 검증

---

## 4. 작업 이력 (최신순)

### 2026-04-13 — NextJS-Front Phase 4 관리자 + 테스트 인프라 + 빌드 검증
- **Phase 4 관리자**: `/admin` 대시보드, `/admin/{members,books,notices,inquiries,events,facility}` 7개 페이지 — ADMIN 롤 가드, 등록/삭제/승인-반려 동작
- **테스트 인프라**: Vitest + jsdom 추가, `vitest.config.ts` (`@/` 에일리어스), [auth.test.ts](NextJS-Front/src/lib/auth.test.ts) — 토큰 저장/로드/클리어 + `getTokenRemainingMs` JWT exp 파싱 9 케이스 모두 통과
- **package.json 스크립트**: `test`, `test:watch` 추가
- **빌드 검증**: `npx tsc --noEmit` 통과, `npx next build` 21 routes 컴파일 성공 (정적 16 + 동적 3 + _not-found + 내부), 타입 오류 0건

### 2026-04-13 — NextJS-Front Phase 1~3 포팅 (초기 구현)
- **로그인 엔드포인트 수정**: 실제 경로는 `/generateToken` (APILoginFilter, `/api` 밖). `AUTH_BASE_URL` 상수 추가, 로그인 → `/api/member/me` 2단계 호출 구현
- **DTO 정합성**: `types/book.ts` `bookImage`→`coverImage`; `member/notice/inquiry/event/rental` 타입 신규 추가
- **Phase 1**: `/signup` (MemberSignupDTO + 중복확인 + base64 프로필), `Navbar`, `Protected`, JWT exp 파싱 자동 로그아웃 (`getTokenRemainingMs`)
- **Phase 2**: `/books` 검색/페이지네이션, `/books/[id]` 대여, `/mypage`, `/mypage/rentals` (반납/연장), `/mypage/edit` (프로필 이미지)
- **Phase 3**: `/notices`(+[id]), `/inquiries`(+[id]/new 비밀글), `/events` 신청
- **루트 레이아웃**: Navbar 통합
- ⚠️ 로컬 `npm install && npm run dev` 로 타입/런타임 검증 필요 (AI 환경 미설치)

### 2026-04-11 — 작업 현황 문서 추가
- `PROGRESS.md` 신설 (본 문서): 전 영역 체크리스트/이력 통합 관리

### 2026-04-11 — NextJS-Front 초기 스캐폴딩 (`a80bb67`)
- Next.js 15 (App Router) + TypeScript + Tailwind 프로젝트 구조 생성
- [NextJS-Front/PLAN.md](NextJS-Front/PLAN.md) 기획/Phase 체크리스트, [README.md](NextJS-Front/README.md) 실행 가이드
- 인프라: `constants/api.ts`, `lib/auth.ts`, `lib/api.ts` (axios + JWT 인터셉터), `lib/auth-context.tsx`
- Phase 1 참고 구현: `/` 홈, `/login`, `/books`
- 웹 환경 주의사항 명시: `10.0.2.2` 사용 금지, 로컬 `localhost`, 모바일 브라우저 실기기는 호스트 사설 IP

### 2026-04-11 — .gitignore 정비 & 빌드 산출물 untrack (`3e53205`)
- 루트 `.gitignore` 신설: `.gradle/`, `build/`, `bin/`, `.omc/`, Flutter/Dart 산출물 제외
- 기존 잘못 추적되던 빌드/IDE 캐시 **339 파일** index 에서 제거
- `NoticeServiceImpl` / `InquiryServiceImpl` delete 메서드 중복 코드 정리 (JOIN FETCH + cascade=ALL 만 유지)
- [SETUP.md](SETUP.md) 신설: 클론/포크, 실행, 에뮬 `10.0.2.2`·실기기 사설 IP 주의사항

### 2026-04-11 — Notice/Inquiry 삭제 FK 오류 & 시설예약 초기 로드 (`6e1d514`)
- **백엔드**: `deleteNotice`/`deleteInquiry` 가 `findById` → `findWithImagesById`/`findWithRepliesById` (JOIN FETCH) 로 변경. LAZY 자식 컬렉션을 로딩해야 cascade=ALL + orphanRemoval 이 안전하게 자식 삭제 후 부모 삭제 수행
- **Flutter**: [admin_facility_screen.dart](Flutter-Front/lib/screen/admin/admin_facility_screen.dart) 초기 `_fetchApplies` 를 `WidgetsBinding.instance.addPostFrameCallback` 으로 지연 호출, 캐시 회피 타임스탬프 + `Cache-Control: no-cache`, `List<dynamic>.from(...)` 참조 변경 유도, `mounted` 가드 추가

### 이전 이력 (요약)
- `4a14533` 마이페이지 프로필 이미지 표시 & 내 정보 수정 화면 개선
- `b238972` 4가지 신규 기능 (프로필 이미지, 마이페이지, 관리자 모드, 이벤트 신청)
- `cb7665b` APIUser 제거 및 Member 엔티티로 인증 통합

---

## 5. 알려진 이슈 / 주의사항

1. **Android 에뮬레이터 전용 IP**: `10.0.2.2` 는 **Flutter Android 에뮬레이터에서만** 동작.
   - Flutter 실기기 → 호스트 사설 IP (예: `192.168.x.x`)
   - Next.js 웹 → `localhost` 또는 호스트 사설 IP
   - iOS 시뮬레이터 → `localhost`
2. **JWT 저장**: 현재 Next.js 는 `localStorage` 사용 중. XSS 취약 가능성 인지.
3. **DB 스키마**: `spring.jpa.hibernate.ddl-auto=update` — 엔티티 변경 시 컬럼 삭제는 수동 필요.
4. **CORS**: Next.js dev(`http://localhost:3000`) 가 Spring CORS 허용 목록에 있는지 작업 전 확인.
5. **공용 Wi-Fi AP 격리**: 학교/카페 공유 네트워크는 Client Isolation 이 활성화되어 실기기 테스트가 차단될 수 있음.

---

## 6. 브랜치 / 원격 정보

- **Main 브랜치**: `main`
- **원격**: `https://github.com/lsy3709/Sample-k9-Flutter-RESTAPI-Project.git`
- **작업 방식**: 포크 사용자는 feature 브랜치로 작업 후 PR, 직접 권한자는 main 에 작은 단위 커밋.
