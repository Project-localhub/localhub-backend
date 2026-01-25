package com.localhub.localhub.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePassword {


    @Schema(example = "123456",description = "현재비밀번호")
    private String currentPassword;
    @Schema(example = "654321",description = "수정할비밀번호")
    private String newPassword;

}
