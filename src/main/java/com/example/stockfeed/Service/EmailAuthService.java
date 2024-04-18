package com.example.stockfeed.Service;

import com.example.stockfeed.Config.RedisUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class EmailAuthService {
    private final JavaMailSender javaMailSender;
    private final RedisUtil redisUtil;
    private static final String FROM_ADDRESS = "bomsprin@gmail.com";
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

    public boolean confirmMail(String mail, int num){
        String email = redisUtil.getData(String.valueOf(num)); //인증번호로 이메일 조회
        if(email != null && email.equals(mail)){ //이메일이 존재하고, 일치하면
            redisUtil.deleteData(String.valueOf(num)); //인증번호 삭제
            return true;
        }
        return false;
    }

}
