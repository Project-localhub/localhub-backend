package com.localhub.localhub.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "message")
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Message  extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sender;
    private String content;

    private Long userId;
    private Long chatroomId;

}
