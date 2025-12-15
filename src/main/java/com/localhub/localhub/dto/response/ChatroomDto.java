package com.localhub.localhub.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class ChatroomDto {


    private Long id;
    private String title;
    private Integer userCount;
    private LocalDateTime createdAt;


}
