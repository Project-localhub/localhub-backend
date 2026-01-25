package com.localhub.localhub.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {

    private String accessToken;

    private Boolean mustChangePassword;

    public LoginResponse(String accessToken){
        this.accessToken = accessToken;
    }
}

