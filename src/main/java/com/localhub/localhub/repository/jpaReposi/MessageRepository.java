package com.localhub.localhub.repository.jpaReposi;

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
                    """

    )
    List<Message> findAllByInquiryChatId(@Param("inquiryChatId") Long inquiryChatId);

}
