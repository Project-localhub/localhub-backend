package com.localhub.localhub.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
@Table(name = "chatroom")
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Chatroom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "title")
    private String title;




}
