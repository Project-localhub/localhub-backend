package com.localhub.localhub.dto.response;

public record JWTResponseDTO(
        String accessToken,
        String refreshToken
) {
}
