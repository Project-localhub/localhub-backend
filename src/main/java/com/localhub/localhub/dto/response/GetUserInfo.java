package com.localhub.localhub.dto.response;

import com.localhub.localhub.entity.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;

@Getter
@AllArgsConstructor
@Builder
public class GetUserInfo {

    private Long id;
    private String username;
    private String name;
    private String email;
    private UserType UserType;



}
