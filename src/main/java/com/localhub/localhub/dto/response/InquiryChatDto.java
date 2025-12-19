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
    private Long userId;
    private LocalDateTime createdAt;



}
