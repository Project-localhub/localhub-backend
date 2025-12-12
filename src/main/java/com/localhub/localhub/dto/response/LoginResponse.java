package com.localhub.localhub.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class LoginResponse {

    private String accessToken;

    public LoginResponse(String accessToken){
        this.accessToken = accessToken;
    }
}

