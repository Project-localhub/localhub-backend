package com.localhub.localhub.repository;

import com.localhub.localhub.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,Long> {


    @Query(value = """
    SELECT CASE 
        WHEN EXISTS(SELECT 1 FROM users WHERE username = :username) 
        THEN 1 
        ELSE 0 
    END
""", nativeQuery = true)
    Long existByUsername(@Param("username") String username);


    @Query
            (value = """
                    SELECT *
                    FROM users u
                    WHERE username = :username
                    
                    """,nativeQuery = true)
    Optional<UserEntity> findByUsername(@Param("username") String username);

}
