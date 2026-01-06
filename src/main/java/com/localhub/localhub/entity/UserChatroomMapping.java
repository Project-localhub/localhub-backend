package com.localhub.localhub.entity;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "user_chatroom_mapping")
@Entity
@Builder
@AllArgsConstructor
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserChatroomMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

     private Long userId;

    private Long chatroomId;
}
