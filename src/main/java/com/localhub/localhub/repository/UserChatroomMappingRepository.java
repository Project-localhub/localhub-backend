package com.localhub.localhub.repository;

import com.localhub.localhub.entity.UserChatroomMapping;
import com.localhub.localhub.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
}
