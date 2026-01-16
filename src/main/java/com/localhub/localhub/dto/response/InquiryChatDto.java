package com.localhub.localhub.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InquiryChatDto {


    private Long id;
    private Long ownerId;
    private String ownerName;
    private Long userId;
    private Long restaurantId;
    private long unreadCount;
    private LocalDateTime createdAt;

    private String lastMessage;
    private LocalDateTime lastMessageTime;


    public void setUnreadCount(Long unreadCount) {
        this.unreadCount = unreadCount;
    }

}
