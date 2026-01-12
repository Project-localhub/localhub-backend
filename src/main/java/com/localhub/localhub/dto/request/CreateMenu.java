package com.localhub.localhub.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateMenu {

    @NotBlank
    private String name;

    @PositiveOrZero
    private Integer price;

    @NotBlank
    private Long restaurantId;
}
