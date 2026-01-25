package com.localhub.localhub.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@AllArgsConstructor
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //이메일형식
    @Column(name = "username" ,unique = true)
    private String username;

    @Column(name = "name")
    private String name;
    //유저 id 개념인 username과는 별개의 email
    @Column(name = "email" , unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "must_change_password",nullable = true)
    private Boolean mustChangePassword;

    @Column(name = "phone")
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,name = "user_type")
    private UserType userType;

    public void update(String email, String name) {
        this.email = email;
        this.name = name;
    }


    public void changePassword(String password) {
        this.password = password;

    }

    public void changeMustChangePassword(boolean mustChangePassword) {
        this.mustChangePassword = mustChangePassword;
    }
}
