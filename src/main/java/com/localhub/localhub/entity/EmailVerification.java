package com.localhub.localhub.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Builder
@Getter
@Table(name = "email_verification")
public class EmailVerification {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code")
    private String code;
    @Column(name = "expired_at")
    private LocalDateTime expiredAt;
    @Column(name = "email")
    private String email;
    @Column(name = "verified")
    private boolean verified;

    public void update(String code, LocalDateTime expiredAt) {
        this.code = code;
        this.expiredAt = expiredAt;
    }


    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiredAt);
    }

    public void verify() {
        this.verified = true;
    }

}
