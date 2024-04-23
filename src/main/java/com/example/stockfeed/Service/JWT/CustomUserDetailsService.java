package com.example.stockfeed.Service.JWT;

import com.example.stockfeed.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> {
                    log.error("User not found with username: {}", username);
                    return new IllegalStateException("해당하는 회원을 찾을 수 없습니다.");
                });
        //return createUserDetails(member);
    }
}
