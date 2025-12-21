package com.localhub.localhub.chattest;

import com.localhub.localhub.dto.response.ChatroomDto;
import com.localhub.localhub.dto.response.InquiryChatDto;
import com.localhub.localhub.entity.*;
import com.localhub.localhub.repository.jpaReposi.ChatRoomRepository;
import com.localhub.localhub.repository.jpaReposi.InquiryChatRepository;
import com.localhub.localhub.repository.jpaReposi.UserChatroomMappingRepository;
import com.localhub.localhub.repository.jpaReposi.UserRepository;
import com.localhub.localhub.service.ChatService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
@ExtendWith(MockitoExtension.class)
public class ChatServiceTest {

    @Mock
    ChatRoomRepository chatRoomRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    InquiryChatRepository inquiryChatRepository;

    @Mock
    UserChatroomMappingRepository userChatroomMappingRepository;

    @InjectMocks
    ChatService chatService;

    @BeforeEach
    void setup() {

        String username = "username";
        UserEntity userEntity = UserEntity.builder()
                .id(10L)
                .username(username)
                .build();
    }


    @Test
    void 채팅방_생성시_방장_자동참가() {

        //given
        String username = "usernmae";
        UserEntity userEntity = UserEntity.builder()
                .id(10L)
                .username(username)
                .build();

        String title = "title";

        Chatroom chatroom = Chatroom.builder()
                .id(1L)
                .title(title)
                .build();

        given(chatRoomRepository.save(any(Chatroom.class)))
                .willReturn(chatroom);

        given(userRepository.findByUsername(any(String.class)))
                .willReturn(Optional.of(userEntity));

        //when
        ChatroomDto result = chatService.createChatroom(username, title);

        //then
        verify(userChatroomMappingRepository).save(any(UserChatroomMapping.class));

    }

    @Test
    void 존재하지않는_유저신청_에러반환() {

        //given

        String username = "usernmae";
        UserEntity userEntity = UserEntity.builder()
                .id(10L)
                .username(username)
                .build();

        String title = "title";

        Chatroom chatroom = Chatroom.builder()
                .id(1L)
                .title(title)
                .build();

        given(chatRoomRepository.save(any(Chatroom.class)))
                .willReturn(chatroom);

        given(userRepository.findByUsername(any(String.class)))
                .willReturn(Optional.empty());
        //when & then

        assertThatThrownBy(() -> chatService.createChatroom(username, title))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void 채팅방_참가시_참가자_저장() {
        //given

        String username = "usernmae";
        UserEntity userEntity = UserEntity.builder()
                .id(10L)
                .username(username)
                .build();

        Long chatroomId = 1L;

        Chatroom chatroom = Chatroom.builder()
                .title("간다ㅏ")
                .build();


        given(userRepository.findByUsername(username))
                .willReturn(Optional.of(userEntity));
        given(chatRoomRepository.findById(chatroomId))
                .willReturn(Optional.of(chatroom));

        //when
        chatService.joinChatroom(username, chatroomId);

        //then

        verify(userChatroomMappingRepository).save(any(UserChatroomMapping.class));


    }

    @Test
    void 이미_참가한_유저면_예외() {

        //given

        String username = "usernmae";
        UserEntity userEntity = UserEntity.builder()
                .id(10L)
                .username(username)
                .build();

        Long chatroomId = 1L;

        Chatroom chatroom = Chatroom.builder()
                .title("간다ㅏ")
                .build();


        given(userRepository.findByUsername(username))
                .willReturn(Optional.of(userEntity));
        given(chatRoomRepository.findById(chatroomId))
                .willReturn(Optional.of(chatroom));
        given(userChatroomMappingRepository.existsByUserIdAndChatroomId(userEntity.getId(), chatroomId))
                .willReturn(true);

        //when & then

        assertThatThrownBy(() -> chatService.joinChatroom(username, chatroomId))
                .isInstanceOf(IllegalArgumentException.class);

    }

    @Test
    void 채팅방이_없으면_예외() {
        //given

        String username = "usernmae";
        UserEntity userEntity = UserEntity.builder()
                .id(10L)
                .username(username)
                .build();

        Long chatroomId = 1L;

        Chatroom chatroom = Chatroom.builder()
                .title("간다ㅏ")
                .build();


        given(userRepository.findByUsername(username))
                .willReturn(Optional.of(userEntity));
        given(chatRoomRepository.findById(chatroomId))
                .willReturn(Optional.empty());
        given(userChatroomMappingRepository.existsByUserIdAndChatroomId(userEntity.getId(), chatroomId))
                .willReturn(false);

        //when & then
        assertThatThrownBy(() -> chatService.joinChatroom(username, chatroomId))
                .isInstanceOf(EntityNotFoundException.class);


    }

    @Test
    void  채팅방_퇴장시_참가자_삭제() {


        String username = "usernmae";
        UserEntity userEntity = UserEntity.builder()
                .id(10L)
                .username(username)
                .build();

        Long chatroomId = 1L;

        Chatroom chatroom = Chatroom.builder()
                .title("간다ㅏ")
                .build();

        given(userRepository.findByUsername(username))
                .willReturn(Optional.of(userEntity));
        given(userChatroomMappingRepository.existsByUserIdAndChatroomId(userEntity.getId(), chatroomId))
                .willReturn(true);
        //when
        chatService.leaveChatroom(username, chatroomId);

        //then
        verify(userChatroomMappingRepository).deleteByUserIdAndChatroomId(userEntity.getId(), chatroomId);



    }

    @Test
    void 참가하지않은_유저_퇴장시_예외() {


        String username = "usernmae";
        UserEntity userEntity = UserEntity.builder()
                .id(10L)
                .username(username)
                .build();

        Long chatroomId = 1L;

        Chatroom chatroom = Chatroom.builder()
                .title("간다ㅏ")
                .build();

        given(userRepository.findByUsername(username))
                .willReturn(Optional.of(userEntity));
        given(userChatroomMappingRepository.existsByUserIdAndChatroomId(userEntity.getId(), chatroomId))
                .willReturn(false);


        //when & then

        assertThatThrownBy(() -> chatService.leaveChatroom(username, chatroomId))
                .hasMessageContaining("유저가 이 방에 참여하고있지않습니다.");
    }

    @Test
    void 유저가_참가한_채팅방_목록_조회_성공() {


        //given
        String username = "usernmae";
        UserEntity userEntity = UserEntity.builder()
                .id(10L)
                .username(username)
                .build();


        Chatroom chatroom1 = Chatroom.builder()
                .id(1L)
                .title("채팅방1")
                .build();

        Chatroom chatroom2 = Chatroom.builder()
                .id(2L)
                .title("채팅방2")
                .build();


        given(userRepository.findByUsername(username))
                .willReturn(Optional.of(userEntity));

        given(chatRoomRepository.findByUserId(10L))
                .willReturn(List.of(chatroom1, chatroom2));


        //when
        List<ChatroomDto> result =
                chatService.getChatroomList(username);


        // then
        assertEquals(2, result.size());
        assertEquals("채팅방1", result.get(0).getTitle());
        assertEquals("채팅방2", result.get(1).getTitle());

        verify(userRepository).findByUsername(username);
        verify(chatRoomRepository).findByUserId(10L);

    }

    @Test
    void 참가한_채팅방이_없는_경우_빈리스트_반환() {
        //given
        String username = "usernmae";
        UserEntity userEntity = UserEntity.builder()
                .id(10L)
                .username(username)
                .build();

        given(userRepository.findByUsername(username))
                .willReturn(Optional.of(userEntity));

        given(chatRoomRepository.findByUserId(10L))
                .willReturn(List.of());

        //when

        List<ChatroomDto> result = chatService.getChatroomList(username);

        //then
        assertEquals(0, result.size());


    }

    @Test
    void 채팅방_조회_유저없음_예외() {
        // given
        given(userRepository.findByUsername(anyString()))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> chatService.getChatroomList(anyString()))
                .hasMessageContaining("존재하지 않는 유저.");

    }


    @Test
    void 고객_점주_문의채팅_생성_성공() {

        //given
        UserEntity customer = UserEntity.builder()
                .id(10L)
                .username("유저")
                .build();


        UserEntity owner = UserEntity.builder()
                .id(11L)
                .username("점주")
                .userType(UserType.OWNER)
                .build();

        given(userRepository.findByUsername(customer.getUsername()))
                .willReturn(Optional.of(customer));

        given(userRepository.findById(owner.getId()))
                .willReturn(Optional.of(owner));
        given(inquiryChatRepository.findByUserIdAndOwnerId(customer.getId(), owner.getId()))
                .willReturn(true);


        //when
        chatService.openInquiryChat(customer.getUsername(), owner.getId());



        //then
        verify(inquiryChatRepository).save(any(InquiryChat.class));


    }
    @Test
    void 존재하지않는_고객이면_예외() {

        //given
        UserEntity customer = UserEntity.builder()
                .id(10L)
                .username("유저")
                .build();


        UserEntity owner = UserEntity.builder()
                .id(11L)
                .username("점주")
                .userType(UserType.OWNER)
                .build();

        given(userRepository.findByUsername(customer.getUsername()))
                .willReturn(Optional.empty());




        //when & then
        assertThatThrownBy(() ->
                chatService.openInquiryChat("유저", 11L)
        )
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void 존재하지않는_점주면_예외() {

        //given
        UserEntity customer = UserEntity.builder()
                .id(10L)
                .username("유저")
                .build();

        UserEntity owner = UserEntity.builder()
                .id(11L)
                .username("점주")
                .userType(UserType.OWNER)
                .build();

        given(userRepository.findByUsername(customer.getUsername()))
                .willReturn(Optional.of(customer));
        given(userRepository.findById(999L))
                .willReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() ->
                chatService.openInquiryChat("유저", 999L)
        )
                .isInstanceOf(EntityNotFoundException.class);

    }

    @Test
    void 점주가_OWNER가_아니면_예외() {


        //given
        UserEntity customer = UserEntity.builder()
                .id(10L)
                .username("유저")
                .build();

        UserEntity owner = UserEntity.builder()
                .id(11L)
                .username("점주")
                .userType(UserType.CUSTOMER)
                .build();

        given(userRepository.findById(owner.getId()))
                .willReturn(Optional.of(owner));
        given(userRepository.findByUsername(customer.getUsername()))
                .willReturn(Optional.of(customer));

        //when & then

        assertThatThrownBy(() -> chatService.openInquiryChat(customer.getUsername(), owner.getId()))
                .isInstanceOf(IllegalStateException.class);



    }

    @Test
    void 이미_존재하는_문의채팅이면_예외() {
        //given
        UserEntity customer = UserEntity.builder()
                .id(10L)
                .username("유저")
                .build();

        UserEntity owner = UserEntity.builder()
                .id(11L)
                .username("점주")
                .userType(UserType.OWNER)
                .build();

        given(userRepository.findById(owner.getId()))
                .willReturn(Optional.of(owner));
        given(userRepository.findByUsername(customer.getUsername()))
                .willReturn(Optional.of(customer));
        given(inquiryChatRepository.findByUserIdAndOwnerId(customer.getId(), owner.getId()))
                .willReturn(true);

        //when & then
        assertThatThrownBy(() -> chatService.openInquiryChat(customer.getUsername(), owner.getId()))
                .isInstanceOf(IllegalStateException.class);

    }

    @Test
    void 문의채팅_목록_조회_성공() {
        // given
        UserEntity user = UserEntity.builder()
                .id(10L)
                .username("유저")
                .build();

        InquiryChat chat = InquiryChat.builder()
                .id(1L)
                .userId(10L)
                .build();

        given(userRepository.findByUsername("유저"))
                .willReturn(Optional.of(user));
        given(inquiryChatRepository.findByUserId(10L))
                .willReturn(List.of(chat));

        // when
        List<InquiryChatDto> result =
                chatService.getInquiryChat("유저");

        // then
        assertEquals(1, result.size());
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result).hasSize(1);
    }


}
