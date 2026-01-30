package com.localhub.localhub.OAuth2;

import com.localhub.localhub.entity.UserEntity;
import com.localhub.localhub.entity.UserRole;
import com.localhub.localhub.entity.UserType;
import com.localhub.localhub.repository.jpaReposi.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        log.error("=== OAUTH2 LOAD USER START ===");

        log.info("[OAuth2] registrationId = {}",
                userRequest.getClientRegistration().getRegistrationId());

        log.info("[OAuth2] accessToken = {}",
                userRequest.getAccessToken().getTokenValue());

        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info(oAuth2User.toString());
        log.info("[OAuth2] raw attributes = {}", oAuth2User.getAttributes());


        String registrationId = userRequest.getClientRegistration().getRegistrationId();


        OAuth2Response oAuth2Response = null;

        if (registrationId.equals("naver")) {

            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());

        } else if (registrationId.equals("google")) {

            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());

        } else if (registrationId.equals("kakao")) {

            oAuth2Response = new KaKaoResponse(oAuth2User.getAttributes());
            log.info("[KAKAO] providerId = {}", oAuth2Response.getProviderId());
            log.info("[KAKAO] email = {}", oAuth2Response.getEmail());
            log.info("[KAKAO] name = {}", oAuth2Response.getName());


        } else {
            return null;
        }

        //리소스 서버에서 발급 받은 정보로 사용자를 특정할 아이디값을 만듬
        String username = oAuth2Response.getProvider()+"_"+oAuth2Response.getProviderId();
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElse(null);

        log.info("[OAuth2] generated username = {}", username);

        if (userEntity == null) {
            log.info("[OAuth2] userEntity exists? = {}", userEntity != null);
            userEntity = UserEntity.builder()
                    .username(username)
                    .email(oAuth2Response.getEmail())
                    .name(oAuth2Response.getName())
                    .userType(UserType.CUSTOMER)
                    .role(UserRole.USER)
                    .build();

            userRepository.save(userEntity);

            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(username);
            userDTO.setName(userEntity.getName());
            userDTO.setRole("ROLE_USER");

            return new CustomOAuth2User(userDTO,oAuth2User.getAttributes());


        } else {

            userEntity.update
                    (oAuth2Response.getEmail(), oAuth2Response.getName());
            log.info("[OAuth2] save user email = {}", oAuth2Response.getEmail());
            log.info("[OAuth2] save user name = {}", oAuth2Response.getName());
            userRepository.save(userEntity);

            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(userEntity.getUsername());
            userDTO.setName(oAuth2Response.getName());
            userDTO.setRole(userEntity.getRole().toString());

            return new CustomOAuth2User(userDTO,oAuth2User.getAttributes());
        }
    }
}
