package com.localhub.localhub.controller;

import com.localhub.localhub.dto.request.ChangePassword;
import com.localhub.localhub.dto.request.ChangeTypeDto;
import com.localhub.localhub.dto.response.ApiResponse;
import com.localhub.localhub.dto.response.GetUserInfo;
import com.localhub.localhub.jwt.CustomUserDetails;
import com.localhub.localhub.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
@Slf4j
@Controller
@ResponseBody
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final AuthService authService;




    @Operation(summary = "유저 타입 변경 CUSTOMER <-> OWNER"
            , description = "CUSTOMER , OWNER를 지정")
    @PutMapping("/changeUserType")
    public ResponseEntity<String> changeUserType
            (@RequestBody ChangeTypeDto changeTypeDto,
             Authentication authentication) {

        String name = authentication.getName();
        authService.changeUserType(changeTypeDto, name);
        return ResponseEntity.ok("유저타입이 변경 되었습니다 : " + changeTypeDto.getChangeUserType());

    }

    @Operation(summary = "유저정보 확인", description = """
            name,username,email,password 확인
            """)
    @GetMapping("/getUserInfo")
    public ResponseEntity<GetUserInfo> getUserInfo(Authentication authentication) {
        log.info("getuerinfo 컨트롤러호출");
        GetUserInfo userInfo = authService.getUserInfo(authentication.getName());
        return ResponseEntity.ok(userInfo);
    }

    @Operation(summary = "비밀번호 변경")
    @PutMapping("/changePassword")
    public ResponseEntity<?> changePassword(

            @RequestBody ChangePassword changePassword,
            Authentication authentication
    ) {

        authService.changePassword(authentication.getName(),changePassword);
        return ResponseEntity.ok("비밀번호가 변경되었습니다.");

    }

}
