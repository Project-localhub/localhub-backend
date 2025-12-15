package com.localhub.localhub.AuthIntegreationTest;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

import java.time.Instant;

public class TestOAuth2UserRequest {

    public static OAuth2UserRequest google() {

        ClientRegistration registration =
                ClientRegistration.withRegistrationId("google")
                        .clientId("test")
                        .clientSecret("test")
                        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                        .redirectUri("test")
                        .authorizationUri("test")
                        .tokenUri("test")
                        .userInfoUri("test")
                        .userNameAttributeName("sub")
                        .clientName("google")
                        .build();

        OAuth2AccessToken token = new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                "test-token",
                Instant.now(),
                Instant.now().plusSeconds(60)
        );

        return new OAuth2UserRequest(registration, token);
    }
}
