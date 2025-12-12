package com.localhub.localhub.dto.request;


import com.localhub.localhub.entity.UserRole;
import com.localhub.localhub.entity.UserType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class JoinDto {

    @Schema(description = "유저 아이디", example = "test")
    private String username;
    @Schema(description = "비밀번호", example = "123456")
    private String password;
    @Schema(description = "이메일", example = "test@test.com")
    private String email;
    @Schema(description = "휴대폰번호 - 없이", example = "01012435678")
    private String phone;
    @Schema(description = "사업자or고객 CUSTOMER OR OWNER", example = "CUSTOMER")
    private UserType userType;
    @Schema(description = "유저 이름", example = "홍길동")
    private String name;
}
