package com.localhub.localhub.service;

import com.localhub.localhub.dto.response.ChatroomDto;
import com.localhub.localhub.entity.Chatroom;
import com.localhub.localhub.entity.UserChatroomMapping;
import com.localhub.localhub.entity.UserEntity;
import com.localhub.localhub.repository.ChatRoomRepository;
import com.localhub.localhub.repository.UserChatroomMappingRepository;
import com.localhub.localhub.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserChatroomMappingRepository userChatroomMappingRepository;
    private final UserRepository userRepository;
    @Transactional
    public ChatroomDto createChatroom(String username, String title) {

        Chatroom chatroom = Chatroom.builder()
                .title(title)
                .build();
       chatroom = chatRoomRepository.save(chatroom);

        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 유저입니다."));


        UserChatroomMapping userChatroomMapping = UserChatroomMapping.builder()

                .userId(userEntity.getId())
                .chatroomId(chatroom.getId())
                .build();
        userChatroomMappingRepository.save(userChatroomMapping);
        return ChatroomDto.builder()
                .id(chatroom.getId())
                .createdAt(chatroom.getCreatedAt())
                .build();

    }
    @Transactional
    public void joinChatroom(String username, Long chatRoomId) {

        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 유저입니다."));



        if (userChatroomMappingRepository.existsByUserIdAndChatroomId(userEntity.getId(), chatRoomId)) {
            log.info("이미 참여한 채팅방입니다.");
            throw new IllegalArgumentException("이미 참여한 채팅방입니다.");
        }



        Chatroom chatroom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 채팅방입니다."));

        UserChatroomMapping userChatroomMapping = UserChatroomMapping.builder()
                .userId(userEntity.getId())
                .chatroomId(chatroom.getId())
                .build();

        userChatroomMappingRepository.save(userChatroomMapping);
    }

    @Transactional
    public void leaveChatroom(String username, Long chatroomId) {

        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 유저."));


        if (!userChatroomMappingRepository.existsByUserIdAndChatroomId(userEntity.getId(), chatroomId)) {
            log.info("참여하지 않은 방입니다.");
            throw new IllegalArgumentException("유저가 이 방에 참여하고있지않습니다.");
        }

        userChatroomMappingRepository.deleteByUserIdAndChatroomId(userEntity.getId(),chatroomId);
    }

    public List<ChatroomDto> getChatroomList(String username) {
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 유저."));

        List<Chatroom> list = chatRoomRepository.findByUserId(userEntity.getId());

        List<ChatroomDto> dto = list.stream().map(chatroom ->
                ChatroomDto.builder()
                        .id(chatroom.getId())
                        .createdAt(chatroom.getCreatedAt())
                        .title(chatroom.getTitle())
                        .build()).toList();
        return dto;

    }

}
