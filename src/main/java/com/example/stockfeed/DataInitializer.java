package com.example.stockfeed;

import com.example.stockfeed.Domain.User;
import com.example.stockfeed.Domain.UserRole;
import com.example.stockfeed.Repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        User user = User.builder()
                .email("user1@gmail.com)")
                .password(passwordEncoder.encode("1234"))
                .name("user1")
                .role(UserRole.valueOf("ROLE_USER"))
                .build();
        User user2 = User.builder()
                .email("user2@gmail.com)")
                .password(passwordEncoder.encode("1234"))
                .name("user2")
                .role(UserRole.valueOf("ROLE_USER"))
                .build();
        userRepository.save(user);
        userRepository.save(user2);
    }
}