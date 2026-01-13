package com.localhub.localhub.controller;

import com.localhub.localhub.dto.response.ChatMessageDto;
import com.localhub.localhub.dto.response.ChatroomDto;
import com.localhub.localhub.dto.response.InquiryChatDto;
import com.localhub.localhub.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    @Operation(summary = "가게 문의채팅생성")
    @PostMapping("/createInquiry/{restaurantId}")
    public ResponseEntity<String> openInquiryChat(Authentication authentication,
                                                  @PathVariable("restaurantId") Long restaurantId) {
        chatService.openInquiryChat(authentication.getName(), restaurantId);
        return ResponseEntity.ok("문의채팅생성.");
    }

    //    @Operation(summary = "채팅방생성", description = """
//            클라이언트에게 title(채팅방제목)을 request로 받고 채팅방생성,
//            채팅방 생성과 동시에 생성자는 채팅방참여.
//            """)
    @PostMapping("/create")
    public ResponseEntity<String> createChatroom(Authentication authentication,
                                                 @RequestParam String title) {

        chatService.createChatroom(authentication.getName(), title);
        return ResponseEntity.ok("채팅방이 생성되었습니다.");
    }

//    @Operation(summary = "채팅방 참가", description = """
//            로그인된 유저가 채팅방id(chatroomId)를 request로 받고 채팅방참가
//            """)
    @PostMapping("/{chatroomId}")
    public ResponseEntity<String> joinChatroom(Authentication authentication,
                                               @PathVariable("chatroomId") Long chatroomId) {

        chatService.joinChatroom(authentication.getName(), chatroomId);
        return ResponseEntity.ok("채팅방 참가");
    }

    @Operation(summary = "채팅방 나가기", description = """
            로그인된 사용자가 채팅방id(chatroomId)를 request로 받고
            채팅방 퇴장
            """)
    @DeleteMapping("/{chatroomId}")
    public ResponseEntity<String> leaveChatroom(Authentication authentication,
                                                @PathVariable("chatroomId") Long chatroomId) {

        chatService.leaveChatroom(authentication.getName(), chatroomId);
        return ResponseEntity.ok("채팅방에서 퇴장하였습니다.");
    }

//    @Operation(summary = "채팅방 목록 조회", description = "참가하고있는 채팅방 목록 조회")
    @GetMapping
    public ResponseEntity<List<ChatroomDto>> getChatroomList(Authentication authentication) {

        List<ChatroomDto> chatroomList = chatService.getChatroomList(authentication.getName());
        return ResponseEntity.ok(chatroomList);
    }

    @Operation(summary = "문의채팅방 목록 조회", description = "문의채팅 목록 조회")
    @GetMapping("/inquiryChat")
    public ResponseEntity<List<InquiryChatDto>> getInquiryChat(Authentication authentication) {

        List<InquiryChatDto> result = chatService.getInquiryChat(authentication.getName());

        return ResponseEntity.ok(result);

    }
    @Operation(summary = "채팅목록 조회" , description = """
            해당 채팅방의 채팅목록 조회
            """)
    @GetMapping("/inquiryChat/{inquiryChatId}/messages")
    public List<ChatMessageDto> getMessageList(@PathVariable("inquiryChatId") Long inquiryChatId) {
        return chatService.getMessageList(inquiryChatId);
    }
}
