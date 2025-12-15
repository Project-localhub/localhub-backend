package com.localhub.localhub.controller;

import com.localhub.localhub.dto.request.ChangeTypeDto;
import com.localhub.localhub.dto.response.GetUserInfo;
import com.localhub.localhub.jwt.CustomUserDetails;
import com.localhub.localhub.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
        authService.changeUserType(changeTypeDto,name);
        return ResponseEntity.ok("유저타입이 변경 되었습니다 : " + changeTypeDto.getChangeUserType());

    }

    @Operation(summary = "유저정보 확인", description = """
            name,username,email,password 확인
            """)
    @GetMapping("/getUserInfo")
    public ResponseEntity<GetUserInfo> getUserInfo(Authentication authentication) {
        GetUserInfo userInfo = authService.getUserInfo(authentication.getName());
        return ResponseEntity.ok(userInfo);
    }
}
