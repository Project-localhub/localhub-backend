package com.localhub.localhub.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "user_chatroom_mapping")
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserChatroomMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

     private Long userId;

    private Long chatroomId;
}
