# 프로젝트 진행 현황

## 2026-11-25

- 기능 제안, 버그 보고, 개발 작업용 GitHub 이슈 양식과 변경 이유 및 검증 결과를 기록하는 PR 템플릿을 추가했다.
- [Git 작업 흐름](git-workflow.md)에 이슈부터 PR 검증까지의 절차와 반복 작업의 스킬 도입 기준을 정리했다.

### 첫 버전 API 후보 (논의 중)

[첫 버전의 이용 흐름](../README.md#첫-버전의-이용-흐름)을 기준으로 정리한 구현 예정 후보이다. 각 행에는 해당 업무에 필요한 API를 나열했다.

| 업무 | 사용자 | API 후보 |
| --- | --- | --- |
| 계정 (포함 확정) | 고용주·사무소·근로자 | 이메일·비밀번호 회원가입 및 로그인 |
| 인력 요청 | 고용주 | 사무소 목록 조회, 선택한 사무소에 인력 요청 등록 |
| 공고 게시 | 사무소 | 받은 인력 요청 목록·상세 조회, 요청을 바탕으로 공고 게시 |
| 공고 탐색과 지원 | 근로자 | 날짜별 공고 목록 조회, 공고 상세 조회, 직행·픽업을 선택해 지원 |
| 배정 심사 | 사무소 | 공고별 지원자 목록 조회, 지원자의 근무 이력·리뷰 조회, 배정 확정 |
| 배정 결과 확인 | 근로자 | 내 지원 목록과 상태 조회, 확정된 현장의 집결 장소·시각 조회 |
| 출석 관리 | 고용주 | 현장별 배정 명단 조회, 배정 근로자의 이전 리뷰 조회, 출석 기록 |
| 근무 후기 | 고용주·근로자 | 고용주의 근무별 별점·후기 등록, 근로자의 받은 후기 조회와 답글 등록 |

배정 확정에 성공하면 시간이 겹치는 다른 지원을 자동 취소하고 근로자에게 확정 알림을 보낸다. 알림 전달 방식은 추후 논의한다.

### 합의한 구현 순서

1. 이메일·비밀번호 회원가입과 로그인을 구현한다.
2. 로그인한 고용주가 사무소 목록을 조회하고 선택한 사무소에 인력 요청을 등록할 수 있도록 한다.
3. 인력 요청 등록 흐름을 완성한 뒤 소셜 로그인 제공자 한 곳을 연동한다. 제공자는 추후 결정한다.

두 로그인 방식 모두 오늘현장 회원 ID로 사용자를 식별한다. 고용주·사무소 담당자·근로자의 역할은 오늘현장에서 관리한다.

### 이메일 가입 및 로그인 구현

- `GET /api/auth/csrf`로 CSRF 토큰을 얻는다. 같은 세션 쿠키를 유지하고 POST 요청에 `X-CSRF-TOKEN` 헤더를 보낸다. 로그인 성공 후 다음 POST 전에는 토큰을 다시 조회한다.
- `POST /api/members`는 JSON으로 email, password(8~128자), role(WORKER 또는 EMPLOYER)을 받아 201과 memberId를 반환한다. 이메일은 소문자로 정규화하고 DB 고유 제약으로 중복 가입을 막는다(409). OFFICE 공개 가입은 권한 부여 방식 결정 전까지 403을 반환한다.
- `POST /api/auth/login`은 폼 데이터(email, password)로 인증한다. 성공하면 세션 인증을 설정하고 204, 실패하면 401을 반환한다.
- `GET /api/auth/me`는 현재 세션의 memberId와 role을 반환한다. 익명 요청은 401이다.
- 로컬 계정 데이터는 파일 기반 H2에 저장하고 비밀번호는 PBKDF2로 해시한다. 통합 테스트는 메모리 H2를 사용한다.

## 2026-11-26

- 회원가입 서비스와 저장소 이름을 MemberRegistrationService, MemberRepository, JdbcMemberRepository로 정리했다.
- 전역 보안 설정은 common.config.SecurityConfig로 옮기고, 로그인에 필요한 회원 조회는 account.infrastructure.MemberUserDetailsService에 두었다.
- 회원 API의 요청·응답 DTO를 account.presentation.dto 패키지의 SignupRequest, SignupResponse, CurrentMemberResponse로 분리했다.
- 회원가입 컨트롤러의 별도 예외 처리 메서드를 제거하고 가입 요청에서 403·409 응답을 직접 반환하도록 정리했다.

### 회원 저장소 JPA 전환

- 회원 도메인과 members 테이블 매핑을 분리했다. MemberRepositoryImpl이 MemberEntity와 Member 간 변환을 담당하고 MemberJpaRepository가 저장·조회한다. 기존 schema.sql을 유지하고 Hibernate가 시작 시 매핑을 검증한다.

### 공통 오류 응답

- API 오류 본문은 code와 message를 가진 JSON이다. 공통 MVC 예외 핸들러가 잘못된 입력과 JSON을 400 INVALID_REQUEST로 변환한다. 전역 예외 핸들러는 중복 이메일을 409 EMAIL_ALREADY_REGISTERED, 사무소 공개 가입을 403 OFFICE_REGISTRATION_RESTRICTED로 변환한다.
- Security 필터에서도 같은 응답 형식을 사용한다. 미인증 접근은 401 AUTHENTICATION_REQUIRED, 잘못된 로그인 정보는 401 INVALID_CREDENTIALS, CSRF 검증 실패는 403 ACCESS_DENIED로 응답한다.
- 공통 ErrorCode는 오류의 이름·메시지·분류를 정의한다. 업무 오류 발생 시 모듈별 enum(회원은 AccountErrorCode)을 BusinessException에 담아 던지면 전역 핸들러가 분류에 맞는 HTTP 상태와 JSON으로 변환한다. MVC 입력 오류와 Security 인증·인가 오류에는 CommonErrorCode를 사용한다.

## 2026-11-27

### 서비스 입출력 객체 분리

- 회원가입 Command·Result를 독립 파일로 분리하고 개발 컨벤션에 명명 규칙을 기록했다.

### 2단계 · 고용주의 사무소 조회와 인력 요청 등록

- 사무소는 이름과 고유 ID를 가지며, 기존 로그인 체계의 OFFICE 회원 한 명을 전용 계정으로 사용한다. DB 외래 키와 고유 제약으로 계정 역할과 1:1 연결을 보장한다. 사무소 개설과 전용 계정 발급 절차는 추후 결정한다.
- 로그인한 고용주가 `GET /api/offices`를 호출하면 사무소의 officeId와 name 목록을 이름순으로 받는다. 등록된 사무소가 없으면 빈 배열을 반환한다.
- 목록에서 고른 officeId로 `POST /api/offices/{officeId}/labor-requests`를 호출한다. 로그인 세션 쿠키와 로그인 후 재발급받은 CSRF 토큰이 필요하다. 서버는 세션의 회원 ID를 고용주로 기록하고 성공 시 201과 requestId를 반환한다.
- 요청 JSON에는 startsAt·endsAt(현지 날짜와 시각, 예: 2026-12-01T22:00), workDescription(작업 내용), workerCount(인원), dailyWage(일당)를 보낸다. 종료는 시작 이후여야 하며 다음 날 종료도 가능하다. 인원과 일당은 양의 정수다.
- 유효하지 않은 입력은 400 INVALID_REQUEST, 존재하지 않는 사무소는 404 OFFICE_NOT_FOUND다. 미인증은 401, 고용주 외 역할과 CSRF 검증 실패는 403이다. POST는 인증·인가보다 CSRF 검사를 먼저 받는다.
- [이슈 #1](https://github.com/nemm3623/SiteToday/issues/1) 검증으로 실제 HTTP 세션에서 두 사무소 중 선택한 곳과 고용주·근무 조건의 H2 저장값, 오류 응답, 사무소 계정의 역할·고유 제약을 확인했다. `./gradlew test --rerun-tasks` 전체 6개 통과.

- 인력 요청 등록의 Command·Result를 독립 파일로 분리했다. 사무소 목록 조회는 서비스의 OfficeSummaryResult를 컨트롤러에서 OfficeSummaryResponse로 변환한다.
