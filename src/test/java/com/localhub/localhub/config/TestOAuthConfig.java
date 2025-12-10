package com.localhub.localhub.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;

@TestConfiguration
public class TestOAuthConfig {

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {

        ClientRegistration fake = ClientRegistration
                .withRegistrationId("test")
                .clientId("test-client-id")
                .clientSecret("test-secret")
                .authorizationUri("https://example.com/auth")
                .tokenUri("https://example.com/token")
                .redirectUri("http://localhost")
                .userInfoUri("https://example.com/userinfo")
                .userNameAttributeName("id")
                .clientAuthenticationMethod(org.springframework.security.oauth2.core.ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(org.springframework.security.oauth2.core.AuthorizationGrantType.AUTHORIZATION_CODE)
                .scope("read")
                .build();

        return new InMemoryClientRegistrationRepository(fake);
    }

}
