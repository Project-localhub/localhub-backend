package com.localhub.localhub.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Builder
@Getter
public class EmailVerification {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String code;

    private LocalDateTime expiredAt;

    private String email;

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
