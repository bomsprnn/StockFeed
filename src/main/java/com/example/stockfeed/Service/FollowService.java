package com.example.stockfeed.Service;

import com.example.stockfeed.Domain.Follow;
import com.example.stockfeed.Domain.User;
import com.example.stockfeed.Repository.FollowRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class FollowService {
    private final FollowRepository followRepository;
    private final UserService userService;

    // 팔로우
    public void follow(Long tofollowId) {
        String username = userService.getCurrentUser();
        User follower = userService.getUser(username); // 팔로우 하는 사람
        Long followerId = follower.getId();

        User following = userService.getUser(tofollowId); // 팔로우 당하는 사람
        Long followingId = following.getId();

        if (followerId.equals(followingId)) {
            throw new IllegalArgumentException("자기 자신을 팔로우할 수 없습니다.");
        }
        if(followRepository.findByFollowerIdAndFollowingId(followerId, followingId).isPresent()) {
            throw new IllegalArgumentException("이미 팔로우한 사용자입니다.");
        }

        Follow follow = Follow.builder()
                .follower(follower)
                .following(following)
                .build();
        followRepository.save(follow);
    }

    // 언팔로우
    public void unfollow(Long toUnfollowId) {
        String username = userService.getCurrentUser();
        User follower = userService.getUser(username); // 언팔로우 하는 사람
        Long followerId = follower.getId();

        User following = userService.getUser(toUnfollowId); // 언팔로우 당하는 사람
        Long followingId = following.getId();

        if (followerId.equals(followingId)) {
            throw new IllegalArgumentException("자기 자신을 언팔로우할 수 없습니다.");
        }
        if(followRepository.findByFollowerIdAndFollowingId(followerId, followingId).isEmpty()) {
            throw new IllegalArgumentException("팔로우하지 않은 사용자입니다.");
        }

        followRepository.deleteByFollowerIdAndFollowingId(followerId, followingId);
    }

    // 팔로우 여부 확인
    public boolean isFollowing(Long followingId) {
        String username = userService.getCurrentUser();
        User follower = userService.getUser(username);
        Long followerId = follower.getId();
        return followRepository.findByFollowerIdAndFollowingId(followerId, followingId).isPresent();
    }

    // 팔로워 수 반환
    public int countFollowers(Long userId) {
        return followRepository.countByFollowingId(userId);
    }
    // 팔로잉 수 반환
    public int countFollowings(Long userId) {
        return followRepository.countByFollowerId(userId);
    }

    // 팔로워 목록 반환
    public List<User> getFollowers(Long userId) {
        List<Follow> followList = followRepository.findByFollowingId(userId);
        List<User> followers = new ArrayList<>();
        for (Follow follow : followList) {
            followers.add(follow.getFollower());
        }
        return followers;
    }


    // 팔로잉 목록 반환
    public List<User> getFollowings(Long userId) {
        List<Follow> followList = followRepository.findByFollowerId(userId);
        List<User> followings = new ArrayList<>();
        for (Follow follow : followList) {
            followings.add(follow.getFollowing());
        }
        return followings;
    }
}
