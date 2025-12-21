package com.localhub.localhub.repository.jpaReposi;

import com.localhub.localhub.entity.InquiryChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InquiryChatRepository extends JpaRepository<InquiryChat, Long> {


    @Query("""
    SELECT EXISTS (
        SELECT 1
        FROM InquiryChat inq
        WHERE inq.userId = :userId
          AND inq.ownerId = :ownerId
    )
""")
    boolean findByUserIdAndOwnerId(@Param("userId") Long userId, @Param("ownerId") Long ownerId);


    @Query
            ("""
                    SELECT inq
                    FROM InquiryChat inq
                    WHERE inq.userId = :userId
                    """)
    List<InquiryChat> findByUserId(@Param("userId") Long userId);


    @Query("""
            SELECT EXISTS (
            SELECT 1
            FROM InquiryChat inq
            WHERE inq.userId = :userId
            AND inq.id = :id
            )
            """)

    boolean existsByUserIdAndInquiryChatroomId(@Param("userId") Long userId, @Param("id") Long inquiryChatroomId);


}
