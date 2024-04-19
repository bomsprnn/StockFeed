package com.example.stockfeed.Service;

import com.example.stockfeed.Domain.Comment;
import com.example.stockfeed.Domain.Post;
import com.example.stockfeed.Domain.User;
import com.example.stockfeed.Repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostService postService;
    private final UserService userService;

    // 댓글 생성
    public void createComment(Long postId, String content) {
        String email = userService.getCurrentUser();
        User user = userService.getUser(email);

        Comment comment = Comment.builder()
                .user(user)
                .content(content)
                .post(postService.getPostById(postId))
                .build();
        commentRepository.save(comment);
    }



}
