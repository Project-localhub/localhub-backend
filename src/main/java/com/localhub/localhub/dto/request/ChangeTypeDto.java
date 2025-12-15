package com.localhub.localhub.dto.request;

import com.localhub.localhub.entity.UserType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeTypeDto {

    private UserType changeUserType;
}
