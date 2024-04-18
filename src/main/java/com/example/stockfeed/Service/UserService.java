package com.example.stockfeed.Service;

import com.example.stockfeed.Domain.User;
import com.example.stockfeed.Domain.UserRole;
import com.example.stockfeed.Dto.SignUpDto;
import com.example.stockfeed.Repository.UserRepository;
import com.example.stockfeed.Service.JWT.JwtProvider;
import com.example.stockfeed.Service.JWT.JwtToken;
import com.example.stockfeed.Service.JWT.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtProvider jwtProvider;
    private final RedisService redisService;
    private final EmailAuthService emailAuthService;

    // 회원가입 시 유효성 검사
    public void checkSignUp(SignUpDto signUpDto) {
        checkavailableEmail(signUpDto.getEmail());
    }

    // 회원가입
    public boolean signUpConfirm(int authNumber) {
        SignUpDto signUpDto = emailAuthService.confirmSignUp(authNumber);
        if (signUpDto == null) {
            return false;
        }

        // 회원 정보를 DB에 저장
        User user = User.builder()
                .email(signUpDto.getEmail())
                .password(passwordEncoder.encode(signUpDto.getPassword()))
                .name(signUpDto.getName())
                .role(UserRole.valueOf("ROLE_USER"))
                .build();
        userRepository.save(user);

        return true;
    }



    // 로그인 ( JWT 토큰 생성 )
    public JwtToken login(String email, String password) {
        userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 E-MAIL 입니다."));

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(email, password);
        log.info("로그인 정보"+authenticationToken.toString()+"이름 "+authenticationToken.getName());
        // 1. Login ID/PW 를 기반으로 Authentication 객체 생성
        // 이때 authentication 는 인증 여부를 확인하는 authenticated 값이 false
        // 2. authenticate 메서드가 실행될 때 CustomUserDetailsService의 loadUserByUsername 메서드가 실행됨
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        log.info("인증 객체 정보"+authenticationManagerBuilder.getObject().toString()+"이름 "+authentication.getName());

        // 3. 인증 객체를 기반으로 JWT 토큰 생성
        return jwtProvider.generateToken(authentication);

    }




    private void checkavailableEmail(String email) { // 유효성 검사
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("이메일은 비워둘 수 없습니다.");
        }
        if (!email.contains("@"))
            throw new IllegalArgumentException("이메일은 @을 포함해야 합니다.");

        if (userRepository.existsByEmail(email))
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
    }


}
