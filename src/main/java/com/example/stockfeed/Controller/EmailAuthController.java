package com.example.stockfeed.Controller;

import com.example.stockfeed.Dto.MailAuthDto;
import com.example.stockfeed.Service.EmailAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class EmailAuthController { //이메일 인증
    private final EmailAuthService emailAuthService;

    /**
     * 이메일 인증번호 전송
     */
    @PostMapping("/email/auth")
    public int sendMailAuth(@RequestBody MailAuthDto mailAuthDto) {
        return emailAuthService.sendMail(mailAuthDto.getReceiver());
    }

    /**
     * 이메일 인증번호 확인
     */
    @PostMapping("/email/auth/confirm")
    public boolean confirmMailAuth(@RequestBody MailAuthDto mailAuthDto) {
        return emailAuthService.confirmMail(mailAuthDto.getReceiver(), mailAuthDto.getNumber());
    }
}
