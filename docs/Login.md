---
title: 로그인 테스트 시나리오
---

## 로그인 테스트 시나리오 (Access / Refresh 발급)

### 로그인 성공 통합테스트

- [x] post_로그인_정상요청시_200반환()
- [x] post_로그인_성공시_accessToken_body로_반환()
- [x] post_로그인_성공시_refreshToken_cookie로_반환()

### 로그인 실패 통합테스트

- [x] post_로그인_존재하지않는아이디_400반환()
- [ ] post_로그인_비밀번호불일치_400반환()
- [ ] post_로그인_username없음_400반환()
- [ ] post_로그인_password없음_400반환()

