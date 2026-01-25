package com.localhub.localhub.dto.request;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePassword {



    private String currentPassword;
    private String changePassword;

}
