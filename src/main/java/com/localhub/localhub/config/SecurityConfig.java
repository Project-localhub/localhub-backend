package com.localhub.localhub.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.localhub.localhub.OAuth2.CustomOAuth2UserService;
import com.localhub.localhub.OAuth2.CustomSuccessHandler;
import com.localhub.localhub.exception.ErrorResponse;
import com.localhub.localhub.jwt.CustomLogoutFilter;
import com.localhub.localhub.jwt.JWTFilter;
import com.localhub.localhub.jwt.JWTUtil;
import com.localhub.localhub.jwt.LoginFilter;
import com.localhub.localhub.repository.jpaReposi.RefreshRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JWTUtil jwtUtil;

    private final AuthenticationConfiguration authenticationConfiguration;

    private final RefreshRepository refreshRepository;

    private final CustomOAuth2UserService customOAuth2UserService;

    private final CustomSuccessHandler customSuccessHandler;


    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
            throws Exception {
        return configuration.getAuthenticationManager();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults());

        http
                .csrf(AbstractHttpConfigurer::disable);

        http
                .formLogin((auth) -> auth.disable());

        http
                .httpBasic((auth) -> auth.disable());

        http
                .oauth2Login((oauth2) -> oauth2
                        .userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig
                                .userService(customOAuth2UserService)
                        )
                        .successHandler(customSuccessHandler)
                        .failureHandler((req, res, ex) -> {
                            ex.printStackTrace(); // 여기 로그 꼭
                            res.sendError(401);
                        })
                );


        http
                .authorizeHttpRequests((auth) -> auth

                        .requestMatchers( // Swagger/OpenAPI 경로 허용
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        .requestMatchers("/swagger-resources/**").permitAll()
                        .requestMatchers("/stomp/**","/chats/**").permitAll()
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/oauth2/**").permitAll()
                        .requestMatchers("/login/**").permitAll()
                        .requestMatchers("/join").permitAll()
//                        .requestMatchers("/stomp/**").permitAll()
                        .requestMatchers("/reissue").permitAll()
                        .requestMatchers("/admin").hasRole("ADMIN")
                        .requestMatchers("/mail/**").permitAll()
                        .requestMatchers("/jwt/**").permitAll()
                        //식당 정보조회내용은 전체허용
                        .requestMatchers("/api/restaurant/get-all-restaurantsByFilter",
                                "/api/restaurant/get-all-restaurants","/api/restaurant/details/**").permitAll()

                        .anyRequest().authenticated());



        http
                .addFilterAt
                        (new LoginFilter(authenticationManager(authenticationConfiguration)
                                        ,jwtUtil,refreshRepository),
                                UsernamePasswordAuthenticationFilter.class);

        http
                .addFilterBefore
                        (new JWTFilter(jwtUtil), LoginFilter.class);

        http
                .addFilterBefore(new CustomLogoutFilter(jwtUtil, refreshRepository),
                        LogoutFilter.class);


        http
                .sessionManagement((session) ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.exceptionHandling(ex -> ex
                .authenticationEntryPoint(
                        (req, res, e) -> {

                            Object status = req.getAttribute("javax.servlet.error.status_code");

                            if (status != null) {
                                throw e; // 👉 Spring MVC 쪽으로 넘김
                            }


                    res.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
                    res.setContentType("application/json;charset=UTF-8");
                    res.getWriter().write("""
                {"status":401,"code":"UNAUTHORIZED","message":"로그인이 필요합니다.","path":"%s"}
                """.formatted(req.getRequestURI()));
                })


                .accessDeniedHandler((req, res, e) -> {
                    res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    res.setContentType("application/json;charset=UTF-8");

                    ErrorResponse errorResponse =
                            new ErrorResponse(403, "권한이 없습니다.");

                    ObjectMapper objectMapper = new ObjectMapper();
                    res.getWriter().write(
                            objectMapper.writeValueAsString(errorResponse)
                    );
                })
        );




        return http.build();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("http://localhost:5173","https://localhub-frontend.vercel.app/"));
        config.setAllowedMethods(List.of("*"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }


}
