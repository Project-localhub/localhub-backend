package com.localhub.localhub.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Table(name = "refresh_entity")
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String refresh;
    private String expiration;
}
