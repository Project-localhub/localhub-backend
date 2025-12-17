package com.localhub.localhub.controller;

import com.localhub.localhub.dto.request.EmailRequest;
import com.localhub.localhub.dto.request.EmailVerifyRequest;
import com.localhub.localhub.service.AuthService;
import com.localhub.localhub.service.MailService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mail")
public class MailController {

    private final MailService mailService;
    private final AuthService authService;

    @Operation(summary = "인증코드 전송", description = "dto로 받은 이메일로 인증코드 전송")
    @PostMapping("/send/verify")
    public ResponseEntity<?> sendEmailVerification(@RequestBody EmailRequest request) {
        mailService.sendEmailVerification(request.getEmail());
        return ResponseEntity.ok("인증 코드가 이메일로 전송되었습니다.");
    }

    @Operation(summary = "인증코드 검증", description = "전송한 인증코드와 이메일과 일치하는지 검증")
    @PostMapping("email/verify")
    public ResponseEntity<?> verifyEmail(@RequestBody EmailVerifyRequest request) {

        authService.verifyEmail(request.getEmail(), request.getCode());
        return ResponseEntity.ok("이메일 인증이 완료되었습니다.");
    }


}
