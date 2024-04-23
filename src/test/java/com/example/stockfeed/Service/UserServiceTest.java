package com.example.stockfeed.Service;

import com.example.stockfeed.Domain.Follow;
import com.example.stockfeed.Dto.SignUpDto;
import com.example.stockfeed.Repository.FollowRepository;
import com.example.stockfeed.Repository.UserRepository;
import com.example.stockfeed.Service.JWT.JwtToken;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    @Autowired
    private FollowService followService;
    @Autowired
    private FollowRepository followRepository;

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
    @Test
    @Transactional
  void 팔로우테스트() {
        List<Follow> followER = followRepository.findByFollowingId(1L).stream().toList();
        System.out.println("follow = " + followER.get(0).getFollower());
        //System.out.println("follow = " + follow.get(1).getFollowing().getName());
    }





}