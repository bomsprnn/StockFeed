package com.example.stockfeed.Controller;

import com.example.stockfeed.Service.FollowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/user") // 기본 경로
public class FollowController {
    private final FollowService followService;

    /**
     * 팔로우
     */
    @PostMapping("/follow")
    public void follow(Long tofollowId) {
        followService.follow(tofollowId);
    }

    /**
     * 언팔로우
     */
    @PostMapping("/unfollow")
    public void unfollow(Long toUnfollowId) {
        followService.unfollow(toUnfollowId);
    }

    /**
     * 사용자가 팔로우하는 사용자 목록 조회
     */
    @GetMapping("/following")
    public void followingList(Long userId) {
        followService.getFollowings(userId);
    }

    /**
     * 사용자를 팔로우하는 사용자 목록 조회
     */
    @GetMapping("/follower")
    public void followerList(Long userId) {
        followService.getFollowers(userId);
    }



}
