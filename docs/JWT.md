---
title: 회원가입 및 JWT & Refresh Token 테스트 시나리오
---

# JWT & Refresh Token Test Scenarios

## 1. 기능 개요
- Access Token / Refresh Token 기반 인증·인가 기능 전체 검증
- 로그인 → Access 발급 → 보호된 API 접근 → 만료 → 재발급 → 재요청까지의 흐름 테스트
- Controller 통합 테스트(MockMvc) + JWT Filter 단위/통합 검증 포함

---

## 2. 테스트 시나리오 (TDD Checklist)

### 회원가입 통합 테스트
- [x] post_회원가입성공시_200반환()
- [x] post_회원가입시_이미존재하는_아이디면_400_반환()


### 회원가입 단위 테스트
-[] post_회원가입_성공후_정상적으로_값들어가있는지조회()
-[] post_UserType이_없으면_에러()


### 로그인(Access + Refresh 발급) 테스트
- [x] post_로그인성공시_200반환()
- [x] post_로그인성공시_access_header담김()
- [x] post_로그인성공시_refresh_cookie담김()
- [x] post_로그인_잘못된비밀번호면_401반환()
- [x] post_로그인_존재하지않는아이디면_401반환()

---

### Access Token 인증 흐름 테스트
- [x ] get_access_정상토큰으로_보호API요청_200반환()
- [x ] get_access_없는토큰요청시_401또는403반환(SecurityConfig에따라)
- [x ] get_access_만료토큰요청시_401반환()
- [x ] get_access_category오류토큰요청시_401반환()

---

### Refresh Token 재발급 테스트
- [x] post_refresh_정상refresh요청시_새access반환()
- [x] post_refresh_없는refresh요청시_400반환()
- [x] post_refresh_만료refresh요청시_400반환()
- [x] post_refresh_category오류_refresh요청시_400반환()


---

### JWT Utility 단위 테스트
- [x] jwtutil_createJwt_claim정상생성()
- [x] jwtutil_isExpired_만료예외발생()
- [x] jwtutil_getUsername_정상추출()
- [x] jwtutil_getRole_정상추출()

---
