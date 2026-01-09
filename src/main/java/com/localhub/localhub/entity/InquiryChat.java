package com.localhub.localhub.entity;

import jakarta.persistence.*;
import lombok.*;


@Table(name = "inquiry_chat")
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class InquiryChat extends BaseEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "restaurant_id")
    private Long restaurantId;
    @Column(name = "user_id")
    private Long userId;


}
