package com.localhub.localhub.repository.jpaReposi;

import com.localhub.localhub.entity.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification,Long> {


    @Query
            ("""
                    SELECT ev
                    FROM EmailVerification ev
                    WHERE ev.email = :email
                    """)
    Optional<EmailVerification> findByEmail(@Param("email") String email);

}
