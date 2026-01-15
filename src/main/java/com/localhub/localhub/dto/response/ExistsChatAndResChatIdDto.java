package com.localhub.localhub.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ExistsChatAndResChatIdDto {

    private boolean isExist;

    private Long id;
}
