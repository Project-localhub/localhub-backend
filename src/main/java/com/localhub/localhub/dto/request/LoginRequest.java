package com.localhub.localhub.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    @Schema(description = "유저아이디",example = "test@email.com")
    private String username;
    @Schema(description = "비밀번호",example = "123456")
    private String password;
}
