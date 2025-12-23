package com.localhub.localhub.AuthIntegreationTest;

import com.localhub.localhub.config.TestExternalConfig;
import com.localhub.localhub.config.TestOAuthConfig;
import com.localhub.localhub.config.TestSecurityConfig;
import com.localhub.localhub.jwt.JWTUtil;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@Import({
        TestExternalConfig.class
})
@DirtiesContext
class JWTUtilTest {

    @Autowired
    JWTUtil jwtUtil;

    @Test
    @DisplayName("createJwt() - JWT 정상 생성")
    void jwtUtil_createJwt_claim정상생성() {

        String token = jwtUtil.createJwt(
                "access",
                "testUser",
                "ROLE_USER",
                60000L
        );

        assertThat(token).isNotNull();
        assertThat(token.split("\\.").length).isEqualTo(3);
    }

    @Test
    @DisplayName("isExpired() - 만료된 토큰 사용 시 예외 발생")
    void jwtUtil_isExpired_만료예외발생() {

        // 이미 만료되도록 1ms 로 생성
        String expiredToken = jwtUtil.createJwt(
                "access",
                "testUser",
                "ROLE_USER",
                1L
        );

        try { Thread.sleep(5); } catch (Exception ignored) {}

        assertThatThrownBy(() -> jwtUtil.isExpired(expiredToken))
                .isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    @DisplayName("getUsername() - 정상 추출")
    void jwtUtil_getUsername_정상추출() {

        String token = jwtUtil.createJwt(
                "access",
                "testUser123",
                "ROLE_USER",
                60000L
        );

        String username = jwtUtil.getUsername(token);

        assertThat(username).isEqualTo("testUser123");
    }

    @Test
    @DisplayName("getRole() - 정상 추출")
    void jwtUtil_getRole_정상추출() {

        String token = jwtUtil.createJwt(
                "access",
                "testUser",
                "ROLE_ADMIN",
                60000L
        );

        String role = jwtUtil.getRole(token);

        assertThat(role).isEqualTo("ROLE_ADMIN");
    }
}