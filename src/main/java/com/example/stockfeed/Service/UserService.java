package com.example.stockfeed.Service;

import com.example.stockfeed.Domain.User;
import com.example.stockfeed.Dto.SignUpDto;
import com.example.stockfeed.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void checkSignUp(SignUpDto signUpDto) { //회원가입
        checkavailableEmail(signUpDto.getEmail());
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

    public int SignUp(SignUpDto signUpDto) {
        User user = User.builder()
                .email(signUpDto.getEmail())
                .password(passwordEncoder.encode(signUpDto.getPassword()))
                .name(signUpDto.getName())
                .build();
        userRepository.save(user);
        return 1;
    }
}
