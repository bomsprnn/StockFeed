package com.example.stockfeed.Service;

import com.example.stockfeed.Dto.SignUpDto;
import com.example.stockfeed.Repository.UserRepository;
import com.example.stockfeed.Service.JWT.JwtToken;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailAuthService emailAuthService;

//    @Test
//    @Transactional
    //@Rollback(value = false)
//    void login() {
//        SignUpDto signUpDto = new SignUpDto();
//        signUpDto.setEmail("loginTest@example.com");
//        signUpDto.setPassword("password");
//        signUpDto.setName("LoginTestName");
//
//        userService.checkSignUp(signUpDto);
//        userRepository.findByEmail("loginTest@example.com");
//
//        JwtToken token = userService.login("loginTest@example.com", "password");
//        System.out.println("token = " + token);
//        Assertions.assertNotNull(token);
//    }






}