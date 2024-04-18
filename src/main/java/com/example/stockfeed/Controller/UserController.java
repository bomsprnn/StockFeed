package com.example.stockfeed.Controller;

import com.example.stockfeed.Config.RedisUtil;
import com.example.stockfeed.Dto.MailAuthDto;
import com.example.stockfeed.Dto.SignUpDto;
import com.example.stockfeed.Service.EmailAuthService;
import com.example.stockfeed.Service.JWT.JwtToken;
import com.example.stockfeed.Service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;
    private final EmailAuthService emailAuthService;
    private final RedisUtil redisUtil;
    private final ObjectMapper objectMapper;

    /**
     * 회원가입
     */

    // 회원가입 1차
    @PostMapping("/signup")
    public void signUp(@RequestBody SignUpDto signUpDto) {
        userService.checkSignUp(signUpDto);
        String key = "REGIST:" + signUpDto.getEmail();
        String value = null;
        try { // 회원가입 정보를 Redis에 저장
            value = objectMapper.writeValueAsString(signUpDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        redisUtil.setDataExpire(key, value, 60 * 6L);
        // 회원 정보 6분동안 저장 (이메일 인증 duration이 5분이므로 6분으로 설정)
        emailAuthService.sendMail(signUpDto.getEmail()); // 이메일 전송
    }

    // 회원가입 2차
    @PostMapping("/signup/confirm")
    public Long signUpConfirm(@RequestBody MailAuthDto mailAuthDto) {
        boolean result = userService.signUpConfirm(mailAuthDto.getNumber());

        return result ? 1L : 0L;
    }

    /**
     * 로그인
     */
    @PostMapping("/login")
    public JwtToken login(@RequestBody SignUpDto signUpDto) {
        return userService.login(signUpDto.getEmail(), signUpDto.getPassword());
    }

    // user 접근 권한 확인
    @PostMapping("user/now")
    public String user() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            log.info("현재 사용자: {}", ((UserDetails) authentication.getPrincipal()).getUsername());
            log.info("현재 사용자 role: {}", ((UserDetails) authentication.getPrincipal()).getAuthorities());

            return ((UserDetails) authentication.getPrincipal()).getUsername();
        } else {
            throw new IllegalStateException("인증된 사용자를 찾을 수 없습니다.");
        }
    }
    /**
     * 로그아웃
     */


}
