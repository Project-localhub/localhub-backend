package com.localhub.localhub.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Table(name = "inquiry_chat")
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class InquiryChat extends BaseEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long ownerId;
    private Long userId;


}
