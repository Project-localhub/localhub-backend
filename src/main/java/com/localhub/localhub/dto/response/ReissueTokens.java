package com.localhub.localhub.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ReissueTokens {

    private String accessToken;

    private String refreshToken;
}
