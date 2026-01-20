package com.localhub.localhub.repository.jpaReposi;

import com.localhub.localhub.entity.RefreshEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RefreshRepositoryJPA extends JpaRepository<RefreshEntity,Long> {

   @Modifying
           @Query("""
        DELETE 
               FROM RefreshEntity r
               WHERE r.refresh = :refreshToken
       """)

    void deleteByRefresh(@Param("refreshToken") String refreshToken);
    @Modifying
    void deleteByUsername(String username);

    @Query("""
        SELECT CASE WHEN  EXISTS
       (
        SELECT 1
        FROM RefreshEntity r
        WHERE r.refresh = :refreshToken
        ) THEN true ELSE false END
""")
    boolean existsByRefresh(@Param("refreshToken") String refreshToken);


}
