package com.localhub.localhub.service;

import com.localhub.localhub.dto.request.EmailVerifyRequest;
import com.localhub.localhub.entity.EmailVerification;
import com.localhub.localhub.repository.EmailVerificationRepository;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.message.SimpleMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;
    private final EmailVerificationRepository emailVerificationRepository;


    public void sendMail(String to, String subject, String text) {


        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setText(text);
            message.setSubject(subject);
            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("이메일 전송 실패 : " + e.getMessage());
        }

    }



    @Transactional
    public void sendVerify(String toEmail, String code) {

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setText("인증코드 6자리 입니다\n\n 코드6자리 [ " + code + "] 입니다. \n 제한시간5분");
            message.setSubject("localhub 이메일 인증 코드 확인 메일입니다.");
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("이메일 전송실패 : "+ e.getMessage());
        }


    }



    @Transactional
    public void sendEmailVerification(String email) {

        String code = String.valueOf(
                ThreadLocalRandom.current().nextInt(100000, 1000000)
        );

        LocalDateTime expiredAt = LocalDateTime.now().plusMinutes(5);


        EmailVerification emailVerification =
                emailVerificationRepository.findByEmail(email)
                .orElse(
                        EmailVerification.builder()
                                .email(email)
                                .code(code)
                                .expiredAt(expiredAt)
                                .verified(false)
                                .build()
                );

        emailVerification.update(code,expiredAt);
        emailVerificationRepository.save(emailVerification);
        sendVerify(email,code);


    }

    public void sendUsername(String toEmail, String username) {

        SimpleMailMessage message = new SimpleMailMessage();

        try {

            message.setTo(toEmail);
            message.setText("localhub에 가입하신 아이디는 " + username + " 입니다.");
            message.setSubject("localhub 아이디 찾기 안내");
            mailSender.send(message);


        } catch (Exception e) {
            throw new RuntimeException("이메일 전송 실패 " + e.getMessage());
        }


    }

    public void sendPassword(String toEmail, String password) {


        SimpleMailMessage message = new SimpleMailMessage();

        try {

            message.setTo(toEmail);
            message.setText("localhub 임시비밀번호 안내 \n 임시비밀번호는" + password + " 입니다.");
            message.setSubject("localhub 비밀번호 찾기 안내");
            mailSender.send(message);


        } catch (Exception e) {
            throw new RuntimeException("이메일 전송 실패 " + e.getMessage());
        }

    }
}
