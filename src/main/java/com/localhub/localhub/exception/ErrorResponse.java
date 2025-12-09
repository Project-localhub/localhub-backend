package com.localhub.localhub.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDateTime;

@Schema(hidden = true)
@Getter
public class ErrorResponse {

    @Schema(description = "HTTP 상태 코드", example = "404")
    private final int status;
    @Schema(description = "에러 메시지", example = "이미 존재하는 값입니다 etc")
    private final String message;
    @Schema(description = "에러 발생 시각", example = "2025-05-11T14:25:00")
    private final LocalDateTime timestamp;

    public ErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
}
