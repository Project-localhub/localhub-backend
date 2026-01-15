package com.localhub.localhub.service;

import com.localhub.localhub.dto.response.ChatMessageDto;
import com.localhub.localhub.dto.response.ChatroomDto;
import com.localhub.localhub.dto.response.ExistsChatAndResChatIdDto;
import com.localhub.localhub.dto.response.InquiryChatDto;
import com.localhub.localhub.entity.*;
import com.localhub.localhub.entity.restaurant.Restaurant;
import com.localhub.localhub.repository.jpaReposi.*;
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
    private final InquiryChatRepository inquiryChatRepository;
    private final MessageRepository messageRepository;
    private final RestaurantRepositoryJpa restaurantRepositoryJpa;
    //문의채팅 생성
    @Transactional
    public ExistsChatAndResChatIdDto openInquiryChat(String customerUsername, Long restaurantId) {

        UserEntity customer = userRepository.findByUsername(customerUsername)
                .orElseThrow(() -> new EntityNotFoundException("유저를 찾을 수 없습니다."));

        Restaurant restaurant = restaurantRepositoryJpa.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("레스토랑을 찾을 수 없습니다."));


        InquiryChat isExistInquiryChat = inquiryChatRepository.findByUserIdAndRestaurantIdReturnId(customer.getId(), restaurantId);

        //존재하는 채팅방이면 기존 챗 id랑 이미 존재한다는 true 반환
        if (isExistInquiryChat != null) {
            return
            ExistsChatAndResChatIdDto.builder()
                    .id(restaurant.getId())
                    .isExist(true)
                    .build();
        }
        //존재하지 않는 채팅방이면 엔티티 생성후 id랑 존재하지 않는다는 false 반환
        InquiryChat inquiryChat = InquiryChat.builder()
                .restaurantId(restaurantId)
                .ownerId(restaurant.getOwnerId())
                .userId(customer.getId())
                .build();
        InquiryChat save = inquiryChatRepository.save(inquiryChat);

        return ExistsChatAndResChatIdDto.builder()
                .id(save.getId())
                .isExist(false)
                .build();
    }


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
    //채팅방 퇴장
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

    //문의채팅방 퇴장
    @Transactional
    public void leaveInquiryChat(String username, Long InquiryChatroomId) {

        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 유저."));



        if (!inquiryChatRepository.existsByUserIdAndInquiryChatroomId(userEntity.getId(), InquiryChatroomId)) {
            log.info("참여하지 않은 방입니다.");
            throw new IllegalArgumentException("유저가 이 방에 참여하고있지않습니다.");
        }

        inquiryChatRepository.deleteById(InquiryChatroomId);

    }

    //채팅 저장




    //채팅방 조회 안씀
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
    //문의채팅목록 조회
    public List<InquiryChatDto> getInquiryChat(String username) {
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 유저."));

        List<InquiryChat> list = inquiryChatRepository.findByUserId(userEntity.getId());

        List<InquiryChatDto> dto = list.stream().map(inquiryChat ->
                InquiryChatDto.builder()
                        .id(inquiryChat.getId())
                        .ownerId(inquiryChat.getOwnerId())
                        .userId(inquiryChat.getUserId())
                        .restaurantId(inquiryChat.getRestaurantId())
                        .createdAt(inquiryChat.getCreatedAt())
                        .build()).toList();
        return dto;

    }
    //채팅메시지 조회
    public List<ChatMessageDto> getMessageList(Long inquiryChatId) {


        inquiryChatRepository.findById(inquiryChatId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지않는 채팅방"));

        List<Message> messages = messageRepository.findAllByInquiryChatId(inquiryChatId);
        return messages.stream().map(ms ->
                ChatMessageDto.builder()
                        .sender(ms.getSender())
                        .message(ms.getContent())
                        .build()
        ).toList();
    }

    //메시지 저장
    public Message saveMessage(String name, Long chatroomId, String message) {


        Message build = Message.builder()
                .sender(name)
                .chatroomId(chatroomId)
                .content(message)
                .build();

        return messageRepository.save(build);

    }
}
