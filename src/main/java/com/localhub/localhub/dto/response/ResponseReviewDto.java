package com.localhub.localhub.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseReviewDto {

        public Long restaurantId;
        public Long userId;
        public String content;
        public double score;
        private String username;
        private String name;
        private LocalDateTime createdAt;
}
