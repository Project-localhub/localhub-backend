package com.localhub.localhub.repository.jpaReposi;

import com.localhub.localhub.dto.response.GetUserInfo;
import com.localhub.localhub.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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


    @Query
            (value = """
                    UPDATE
                    users 
                    SET user_type = :changeUserType
                    where id = :userId
                    """,nativeQuery = true)
    @Modifying
    void updateUserType(@Param("userId") Long userId,
                        @Param("changeUserType") String changeUserType);

    @Query("""
    SELECT new com.localhub.localhub.dto.response.GetUserInfo(
        u.id,
        u.username,
        u.name,
        u.email
    )
    FROM UserEntity u
    WHERE u.id = :id
""")
    Optional<GetUserInfo> getUserInfo(@Param("id") Long id);


    @Query("""
            SELECT u
            FROM UserEntity u
            WHERE u.email = :email
            AND u.name = :name
            """)
    Optional<UserEntity> findByUsernameAndEmail(@Param("email") String email, @Param("name") String name);


    @Query("""
            SELECT u
            FROM UserEntity u
            WHERE u.email = :email
            """)
    Optional<UserEntity> findByEmail(@Param("email") String email);


    @Query("""
         SELECT CASE
         WHEN COUNT(u) > 0 THEN true
         ELSE false
         END  
         FROM UserEntity u
         WHERE u.email = :email
            
            """)
    boolean isExistEmail(@Param("email") String email);

}
