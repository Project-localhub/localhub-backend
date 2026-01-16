package com.localhub.localhub.repository.jpaReposi;

import com.localhub.localhub.dto.response.UnreadCountProjection;
import com.localhub.localhub.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message,Long> {

    @Query(
            """
                    SELECT ms
                    FROM Message ms
                    WHERE ms.chatroomId = :inquiryChatId
                    ORDER BY ms.id ASC
                    """

    )
    List<Message> findAllByInquiryChatId(@Param("inquiryChatId") Long inquiryChatId);

    @Query("""
SELECT
  m.chatroomId AS chatroomId,
  COUNT(m) AS unreadCount
FROM Message m
JOIN UserChatroomMapping ucm
  ON ucm.chatroomId = m.chatroomId
WHERE ucm.userId = :userId
  AND m.id > ucm.lastReadMessageId
  AND m.userId <> :userId
  AND m.chatroomId IN :chatroomIds
GROUP BY m.chatroomId
""")
    List<UnreadCountProjection> countUnreadByChatrooms(
            @Param("userId") Long userId,
            @Param("chatroomIds") List<Long> chatroomIds
    );
}