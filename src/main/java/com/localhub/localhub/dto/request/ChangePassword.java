package com.localhub.localhub.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePassword {


    @Schema(name = "현재비밀번호",example = "123456")
    private String currentPassword;
    @Schema(name = "변경할비밀번호",example = "654321")
    private String newPassword;

}
