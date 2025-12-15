package com.localhub.localhub.OAuth2;

import com.localhub.localhub.entity.RefreshEntity;
import com.localhub.localhub.jwt.JWTUtil;
import com.localhub.localhub.repository.RefreshRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {


    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;


    @Override
    public void onAuthenticationSuccess
            (HttpServletRequest request, HttpServletResponse response,
             Authentication authentication)
            throws IOException, ServletException {
        log.info("[SUCCESS] OAuth2 login success");
        log.info("[SUCCESS] principal = {}", authentication.getPrincipal());

        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();

        String username = customUserDetails.getUsername();

        Collection<? extends GrantedAuthority> authorities = customUserDetails.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        String refresh = jwtUtil.createJwt("refresh", username, role, 86400000L);

        RefreshEntity refreshEntity = RefreshEntity.builder()
                .refresh(refresh)
                .username(username)
                .expiration("86400000L")
                .build();
        refreshRepository.save(refreshEntity);

        String access = jwtUtil.createJwt("access", username, role, 60 * 60 * 60L);

        response.addCookie(createCookie("access", access));
        response.addCookie(createCookie("refresh",refresh));
        log.info("[REDIRECT] redirect to frontend");
        response.sendRedirect("http://localhost:5173/");

    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(60 * 60 * 60);

        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }

}