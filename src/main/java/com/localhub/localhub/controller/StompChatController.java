package com.localhub.localhub.controller;

import com.localhub.localhub.dto.response.ChatMessageDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Slf4j
@Controller
public class StompChatController {

    @MessageMapping("/chats/{chatroomId}")
    @SendTo("/sub/chats/{chatroomId}")
    public ChatMessageDto handleMessage(
            @DestinationVariable Long chatroomId,
            Authentication authentication,
            @Payload Map<String, String> payload
    ) {
               log.info("{} sent {} in {}",authentication.getName(),payload, chatroomId);
        return new ChatMessageDto(authentication.getName(), payload.get("message"));
    }
}
