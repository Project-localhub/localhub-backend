package com.localhub.localhub.controller;

import com.localhub.localhub.dto.request.JoinDto;
import com.localhub.localhub.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "회원가입",description = """
            username(email),phone,UserType(CUSTOMER, OWNER고정),name(유저이름)
            값을 받고 회원가입 로직 진행
            UserType은 필수값(없으면 에러 반환)
            """)
    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody JoinDto joinDto) {
        authService.Join(joinDto);
        return ResponseEntity.ok("회원가입이 성공했습니다.");
    }
}
