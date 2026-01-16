package com.localhub.localhub.repository.jpaReposi;

import com.localhub.localhub.entity.UserChatroomMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserChatroomMappingRepository extends JpaRepository<UserChatroomMapping,Long> {



    @Query("""
            SELECT CASE WHEN COUNT(ucm) > 0 THEN true ELSE FALSE END
            FROM UserChatroomMapping ucm
            WHERE ucm.userId = :userId
            AND ucm.chatroomId = :chatroomId
            """)
    boolean existsByUserIdAndChatroomId(@Param("userId") Long userId,
                                        @Param("chatroomId") Long chatRoomId);


    @Query
            ("""
                    DELETE
                     FROM UserChatroomMapping ucm
                     WHERE ucm.userId = :userId
                     AND ucm.chatroomId =:chatroomId
                    
                    """)
    void deleteByUserIdAndChatroomId(@Param("userId") Long userId,
                                     @Param("chatroomId") Long chatroomId);



    @Query ("""
            SELECT ucm
            FROM UserChatroomMapping ucm
            WHERE ucm.userId = :userId
            AND ucm.chatroomId =:chatroomId
            """)
    boolean isExistInquiryChatroomId(@Param("userId") Long id, @Param("chatroomId") Long id1);


    @Query("""
            SELECT ucm
            FROM UserChatroomMapping ucm
            WHERE ucm.userId = :userId
            AND ucm.chatroomId = :chatroomId
            """)
    Optional<UserChatroomMapping> findByUserIdAndInquiryChatId(@Param("userId") Long userId,
                                                               @Param("chatroomId") Long chatroomId);

    @Query("""
            SELECT ucm.lastReadMessageId 
            FROM UserChatroomMapping ucm
            WHERE ucm.userId = :userId
            """)
    Optional<Long> findLastReadMessageIdByUserId(@Param("userId") Long userId);
}
