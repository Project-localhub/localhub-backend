package com.localhub.localhub.repository;

import com.localhub.localhub.entity.Chatroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomRepository extends JpaRepository<Chatroom, Long> {


    @Query("""
            SELECT cr
            FROM Chatroom cr
            JOIN UserChatroomMapping ucm
            on cr.id = ucm.chatroomId
            WHERE ucm.userId = :userId 
            
            """)
    List<Chatroom> findByUserId(@Param("userId") Long userId);

}
