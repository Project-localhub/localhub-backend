package com.localhub.localhub.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@TestConfiguration
public class TestSecurityConfig {

    @Bean
    SecurityFilterChain testFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .oauth2Login(oauth -> oauth.disable())   // ⭐ 핵심
                .formLogin(form -> form.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated());

        return http.build();
    }
}