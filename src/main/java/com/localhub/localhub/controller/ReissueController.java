package com.localhub.localhub.controller;

import com.localhub.localhub.dto.response.ReissueTokens;
import com.localhub.localhub.jwt.JWTUtil;
import com.localhub.localhub.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ReissueController {

    private final JWTUtil jwtUtil;
    private final AuthService authService;


    @Operation(summary = "토큰 재발급",description = "refresh토큰 유효성 확인후 access,refresh 토큰 재발급")
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {

        //  쿠키에서 refresh token 꺼내기
        String refresh = null;

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refresh")) {
                    refresh = cookie.getValue();
                }
            }
        }

        if (refresh == null) {
            return new ResponseEntity<>
                    ("refresh token null", HttpStatus.BAD_REQUEST);
        }

        //  service에 refresh 전달 → 새 access 발급받기
        ReissueTokens reissueTokens = authService.reissue(refresh);

        Cookie newRefreshCookie =
                createCookie("refresh", reissueTokens.getRefreshToken()
                        , 24 * 60 * 60); // 하루

        response.addCookie(newRefreshCookie);


        return ResponseEntity.ok()
                .body(Map.of("accessToken", reissueTokens.getAccessToken()));
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
