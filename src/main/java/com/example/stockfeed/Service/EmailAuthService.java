package com.example.stockfeed.Service;

import com.example.stockfeed.Config.RedisUtil;
import com.example.stockfeed.Dto.SignUpDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class EmailAuthService {
    private final JavaMailSender javaMailSender;
    private final RedisUtil redisUtil;
    private final ObjectMapper objectMapper;
    @Value("${spring.mail.username}")
    private static String FROM_ADDRESS;
    private static final String TITLE = "StockFeed 회원가입 인증 메일입니다.";
    private static int number;

    public static void createNumber() {
        number = (int) (Math.random() * 1000000); //6자리 난수 생성
    }

    public MimeMessage createMail(String email) {
        createNumber();
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            message.setFrom(FROM_ADDRESS);
            message.setRecipients(MimeMessage.RecipientType.TO, email);
            message.setSubject(TITLE);
            String body = "";
            body += "<h3>" + "인증 번호" + "</h3>";
            body += "<h1>" + number + "</h1>";
            message.setText(body, "UTF-8", "html");

            redisUtil.setDataExpire(String.valueOf(number),email,60*5L); // {key,value} 5분동안 저장.

        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return message;
    }

    public int sendMail(String mail){
        MimeMessage message = createMail(mail); //인증 메일 생성
        javaMailSender.send(message); //인증 메일 발송
        return number; //인증 번호 반환
    }


    public SignUpDto confirmSignUp(int num) {
        String email = redisUtil.getData(String.valueOf(num)); // 인증번호로 이메일 조회
        if (email == null) {
            return null;
        }

        String key = "REGIST:" + email;
        String value = redisUtil.getData(key);
        if (value == null) {
            return null;
        }

        SignUpDto signUpDto;
        try {
            signUpDto = objectMapper.readValue(value, SignUpDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        redisUtil.deleteData(String.valueOf(num)); // 인증번호 삭제
        redisUtil.deleteData(key); // 회원가입 정보 삭제

        return signUpDto;
    }

}
