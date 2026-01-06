package com.localhub.localhub.config;

import com.localhub.localhub.r2.R2StorageController;
import com.localhub.localhub.r2.R2StorageService;
import com.localhub.localhub.r2.StorageProps;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;

import java.util.List;

@TestConfiguration
public class     TestExternalConfig {
        //메일
    @Bean
    JavaMailSender javaMailSender() {
        return Mockito.mock(JavaMailSender.class);
    }

        //소셜
    @Bean
    ClientRegistrationRepository clientRegistrationRepository() {
        return Mockito.mock(ClientRegistrationRepository.class);
    }

    // >>r2 storage
    @Bean
    R2StorageService r2StorageService() {
        return Mockito.mock(R2StorageService.class);
    }

    @Bean
    StorageProps storageProps() {
        return Mockito.mock(StorageProps.class);
    }


    @Bean
    R2StorageController r2StorageController() {
        return Mockito.mock(R2StorageController.class);
    }
    // <<r2
}
