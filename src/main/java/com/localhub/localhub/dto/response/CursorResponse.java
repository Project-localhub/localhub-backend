package com.localhub.localhub.dto.response;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class CursorResponse<T> {

    private T data;
    private Long nextId;
    private Boolean hasNext;


}
