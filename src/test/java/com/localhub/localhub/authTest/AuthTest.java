package com.localhub.localhub.authTest;

import com.localhub.localhub.dto.request.JoinDto;
import com.localhub.localhub.entity.UserEntity;
import com.localhub.localhub.entity.UserType;
import com.localhub.localhub.repository.UserRepository;
import com.localhub.localhub.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
public class AuthTest {

    @InjectMocks
    AuthService authService;

    @Mock
    UserRepository userRepository;

    @Mock
    BCryptPasswordEncoder passwordEncoder;

    @Test
    void 회원가입_성공_저장된값이_DTO와_같은지검증() {

        // given
        JoinDto dto = new JoinDto();
        dto.setUsername("testUser");
        dto.setPassword("encoded");
        dto.setPhone("01012345678");
        dto.setUserType(UserType.CUSTOMER);

        given(passwordEncoder.encode(anyString())).willReturn("encoded");

        // when
        authService.Join(dto);

        // then
        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(captor.capture());

        UserEntity saved = captor.getValue();
        assertEquals(dto.getPassword(), saved.getPassword());
        assertEquals(dto.getUsername(), saved.getUsername());
        assertEquals(dto.getPhone(), saved.getPhone());
        assertEquals(dto.getUserType(), saved.getUserType());
    }


    @Test
    void post_UserType이_없으면_에러() {


        //given
        JoinDto joinDto = new JoinDto();

        joinDto.setUsername("가나다");
        joinDto.setPassword("213413");
        joinDto.setPhone("2134143");



        //when&then
        assertThatThrownBy(() -> authService.Join(joinDto))
                .isInstanceOf(IllegalArgumentException.class);
    }

}
