package com.localhub.localhub.service;

import com.localhub.localhub.dto.request.JoinDto;
import com.localhub.localhub.dto.request.LoginRequest;
import com.localhub.localhub.dto.response.TokenResponse;
import com.localhub.localhub.dto.response.ReissueTokens;
import com.localhub.localhub.entity.RefreshEntity;
import com.localhub.localhub.entity.UserEntity;
import com.localhub.localhub.entity.UserRole;
import com.localhub.localhub.entity.UserType;
import com.localhub.localhub.jwt.JWTUtil;
import com.localhub.localhub.repository.RefreshRepository;
import com.localhub.localhub.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Value("${jwt.expiration}")
    private long accessExpireMs;

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    //회원가입
    public void Join(JoinDto joinDto) {




        String username = joinDto.getUsername();
        String password = joinDto.getPassword();
        UserType userType = joinDto.getUserType();
        String phone = joinDto.getPhone();

        if (userType == null) {
            throw new IllegalArgumentException("userType은 필수값입니다.");
        }


       Long isExist = userRepository.existByUsername(username);

        if (isExist == 1L) {
            throw new IllegalStateException("이미 존재하는 유저입니다.");
        }

        UserEntity user = UserEntity.builder()
                .username(username)
                .password(bCryptPasswordEncoder.encode(password))
                .role(UserRole.USER)
                .email(joinDto.getEmail())
                .name(joinDto.getName())
                .userType(userType)
                .phone(phone)
                .build();

        userRepository.save(user);

    }
    //refresh 토큰 재발급
    public ReissueTokens reissue(String refresh) {

        //  만료 검사
        jwtUtil.isExpired(refresh);

        //  refresh 토큰인지 category 확인
        String category = jwtUtil.getCategory(refresh);
        if (!category.equals("refresh")) {
            throw new IllegalArgumentException("invalid refresh token");
        }

        Boolean isExist = refreshRepository.existsByRefresh(refresh);
        if (!isExist) {
            throw new IllegalArgumentException("invalid refresh token");
        }


        //  사용자 정보 추출
        String username = jwtUtil.getUsername(refresh);
        String role = jwtUtil.getRole(refresh);




        //  새로운 access token 생성 후 반환
        String accessToken =
                jwtUtil.createJwt("access", username, role, accessExpireMs);
        String newRefresh =
                jwtUtil.createJwt("refresh", username, role, 86400000L);

        //Refresh 토큰 저장 DB에 기존의 Refresh 토큰 삭제 후 새 Refresh 토큰 저장
        refreshRepository.deleteByRefresh(refresh);
        addRefreshEntity(username, newRefresh, 86400000L);


        return new ReissueTokens(accessToken, newRefresh);


    }
    //refresh토큰 저장
    private void addRefreshEntity(String username, String refresh, Long expiredMs) {

        Date date = new Date(System.currentTimeMillis() + expiredMs);


        RefreshEntity refreshEntity = RefreshEntity.builder()
                .username(username)
                .refresh(refresh)
                .expiration(date.toString())
                .build();

        refreshRepository.save(refreshEntity);
    }
    //로그인
    @Transactional
    public TokenResponse login(LoginRequest loginRequest) {

        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();


        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new EntityNotFoundException("존재하지 않는 아이디입니다 "));

        if (!bCryptPasswordEncoder.matches(password, userEntity.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 다릅니다. " );
        }

        UserRole role = userEntity.getRole();
        UserType userType = userEntity.getUserType();

        String access = jwtUtil.createJwt("access", username, role.name(), accessExpireMs);
        String refresh = jwtUtil.createJwt("refresh", username, role.name(), 86400000L);

        RefreshEntity refreshEntity = RefreshEntity.builder()
                .username(username)
                .refresh(refresh)
                .expiration("86400000")
                .build();

        refreshRepository.save(refreshEntity);


        return new TokenResponse(access, refresh);

    }
}
