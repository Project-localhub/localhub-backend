package com.localhub.localhub.entity;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "message")
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Message  extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "sender")
    private String sender;
    @Column(name = "content")
    private String content;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "chatroom_id")
    private Long chatroomId;

}
