package com.localhub.localhub.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
@Builder
@Getter
@Setter
@AllArgsConstructor
public class ChatMessageDto {

    private String sender;
    private String message;

}
