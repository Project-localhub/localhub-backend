package com.localhub.localhub.controller;

import com.localhub.localhub.dto.request.JoinDto;
import com.localhub.localhub.dto.request.LoginRequest;
import com.localhub.localhub.dto.response.LoginResponse;
import com.localhub.localhub.dto.response.TokenResponse;
import com.localhub.localhub.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/auth")
@RequiredArgsConstructor
@RestController
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "회원가입", description = """
            username(email),phone,UserType(CUSTOMER, OWNER고정),name(유저이름)
            값을 받고 회원가입 로직 진행
            UserType은 필수값(없으면 에러 반환)
            """)
    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody JoinDto joinDto) {
        authService.Join(joinDto);
        return ResponseEntity.ok("회원가입이 성공했습니다.");
    }

    @Operation(summary = "로그인", description = """
                username(email), password 값으로 받아서 로그인진행,
                로그인 진행후 access Token은 body , refresh Token은 쿠키로 반환
            """)
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest,
                                               HttpServletResponse response) {
        TokenResponse tokenResponse = authService.login(loginRequest);
        Cookie refresh = createCookie("refresh", tokenResponse.getRefresh(), 24 * 60 * 60);
        response.addCookie(refresh);

        return ResponseEntity.ok(new LoginResponse(tokenResponse.getAccess()));
    }


    private Cookie createCookie(String key, String value, int maxAge) {
        Cookie cookie = new Cookie(key, value);
        cookie.setHttpOnly(true);   // JS 접근 X
        cookie.setSecure(true);     // HTTPS 환경이면 true
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        return cookie;
    }

}
