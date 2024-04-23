package com.example.stockfeed;

import com.example.stockfeed.Domain.*;
import com.example.stockfeed.Repository.CommentRepository;
import com.example.stockfeed.Repository.FollowRepository;
import com.example.stockfeed.Repository.PostRepository;
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
    @Autowired
    private FollowRepository followRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private PostRepository postRepository;

    @PostConstruct
    public void init() {
        User user = User.builder()
                .email("user1@gmail.com")
                .password(passwordEncoder.encode("1234"))
                .name("user1")
                .role(UserRole.valueOf("ROLE_USER"))
                .build();
        User user2 = User.builder()
                .email("user2@gmail.com")
                .password(passwordEncoder.encode("1234"))
                .name("user2")
                .role(UserRole.valueOf("ROLE_USER"))
                .build();
        User user3 = User.builder()
                .email("user3@gmail.com")
                .password(passwordEncoder.encode("1234"))
                .name("user3")
                .role(UserRole.valueOf("ROLE_USER"))
                .build();
        User user4 = User.builder()
                .email("user4@gmail.com")
                .password(passwordEncoder.encode("1234"))
                .name("user4")
                .role(UserRole.valueOf("ROLE_USER"))
                .build();
        userRepository.save(user);
        userRepository.save(user2);
        userRepository.save(user3);
        userRepository.save(user4);

        Follow follow = Follow.builder() //1이 2를 팔로우
                .follower(user)
                .following(user2)
                .build();
        Follow follow2 = Follow.builder() //2가 1을 팔로우
                .follower(user2)
                .following(user)
                .build();
        Follow follow3 = Follow.builder() //3이 1을 팔로우
                .follower(user3)
                .following(user)
                .build();
        followRepository.save(follow);
        followRepository.save(follow2);
        followRepository.save(follow3);

        Post post = Post.builder()
                .title("title1")
                .content("content1")
                .user(user)
                .viewCount(0)
                .build();
        Post post2 = Post.builder()
                .title("title2")
                .content("content2")
                .user(user2)
                .viewCount(0)
                .build();
        postRepository.save(post);
        postRepository.save(post2);

        Comment comment = Comment.builder()
                .content("comment1")
                .user(user)
                .build();
        Comment comment2 = Comment.builder()
                .content("comment2")
                .user(user2)
                .build();
        commentRepository.save(comment);
        commentRepository.save(comment2);


    }
}