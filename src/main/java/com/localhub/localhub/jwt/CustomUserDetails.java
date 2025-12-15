package com.localhub.localhub.jwt;


import com.localhub.localhub.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final Long id;
    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;


    public CustomUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities) {

        this.id = null;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }

    public CustomUserDetails(UserEntity userEntity) {

        this.id = userEntity.getId();
        this.username = userEntity.getUsername();
        this.password = userEntity.getPassword();
        this.authorities = List.of
                (new SimpleGrantedAuthority
                        ("ROLE_" + userEntity.getRole().name()));

    }



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }
}
