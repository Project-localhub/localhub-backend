package com.localhub.localhub.entity.restaurant;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
@NoArgsConstructor
@Entity
@Table
public class Restaurant {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;

}
